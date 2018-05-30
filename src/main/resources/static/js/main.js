;
new Vue({
    el: "#main",
    data: {
        errorMessage: "",
        docs: {},
        documentName: "",
        docNameError: "",
        currentDocument: null,
        activeUsers: "",
        documentData: "",
        CommitStatus: {
            noData: 0,
            justLoaded: 1,
            modified: 2,
            pushed: 3,
            fetched: 4
        },
        ProcessStatus: {
            idle: 0,
            pushing: 1,
            fetching: 2
        },
        commitStatus: 0,
        processStatus: 0,
        ignoreWatchOnce: false,
        pushInterval: 3000,
        fetchInterval: 1000,
        username: ""
    },
    computed: {},
    watch: {
        documentData: function () {
            if (this.ignoreWatchOnce === true) {
                this.ignoreWatchOnce = false;
                return;
            }

            this.commitStatus = this.CommitStatus.modified;
        }
    },
    methods: {
        pushChanges: function () {
            if (this.currentDocument
                && this.commitStatus == this.CommitStatus.modified
                && this.processStatus == this.ProcessStatus.idle) {
                console.log("pushing!");
                this.pushDocument();
            }
        },
        fetchChanges: function () {

            // always
            this.updateDocList();

            if(this.currentDocument) {
                this.updateActiveUsers();
            }

            if (this.currentDocument
                && this.checkFetchingAvailability()) {
                console.log("requesting last version");
                var vm = this;
                axios.get('/rest/docs/' + this.currentDocument.id + "/lastupdate")
                    .then(function (response) {
                        if (response.data > vm.currentDocument.lastModification
                            && vm.checkFetchingAvailability()) {
                            console.log("detected new version");
                            vm.commitStatus = vm.CommitStatus.fetched;
                            vm.loadDocument(vm.currentDocument.id, vm.CommitStatus.fetched);
                        }
                    })
                    .catch(function (reason) {
                        vm.showError(reason);
                    })
            }
        },
        checkFetchingAvailability: function () {
            return (this.commitStatus == this.CommitStatus.pushed
                || this.commitStatus == this.CommitStatus.fetched
                || this.commitStatus == this.CommitStatus.justLoaded)
                && this.processStatus == this.ProcessStatus.idle;
        },
        showError: function (reason) {
            if (reason.response) {
                this.errorMessage = reason.response.data.message;
            } else {
                this.errorMessage = reason.message;
            }

            alert(this.errorMessage);
        },
        closeError: function () {
            this.errorMessage = "";
        },
        createDocument: function () {
            var vm = this;
            if (this.documentName.length > 0) {
                axios.post('/rest/docs/new', {
                    id: 0,
                    name: vm.documentName,
                    data: ""
                })
                    .then(function (value) {
                        vm.documentName = "";
                        vm.updateDocList();
                    })
                    .catch(function (reason) {
                        vm.showError(reason);
                    });
            } else {
                vm.docNameError = "Document name is empty. Try valid non-empty name.";
            }
        },
        updateDocList: function () {
            var vm = this;
            axios.get("/rest/docs")
                .then(function (response) {
                    vm.docs = response.data;
                })
                .catch(function (reason) {
                    vm.showError(reason);
                });
        },
        closeDocNameWarning: function () {
            this.docNameError = "";
        },
        loadDocument: function (id, commitStatus, updateVersionOnly) {
            var vm = this;
            vm.processStatus = vm.ProcessStatus.fetching;
            axios.get("/rest/docs/" + id)
                .then(function (response) {
                    if (!commitStatus) {
                        commitStatus = vm.CommitStatus.justLoaded;
                    }
                    vm.ignoreWatchOnce = true;
                    vm.currentDocument = response.data;
                    vm.documentData = vm.currentDocument.data;

                    vm.commitStatus = commitStatus;
                    vm.processStatus = vm.ProcessStatus.idle;
                })
                .catch(function (reason) {
                    vm.showError(reason);
                });
        },
        pushDocument: function () {
            if (this.currentDocument !== null) {
                var vm = this;
                vm.currentDocument.data = vm.documentData;
                vm.processStatus = vm.ProcessStatus.pushing;
                vm.commitStatus = vm.CommitStatus.pushed;

                // we don't need to save document with old version labels
                // just leave it empty for now
                vm.currentDocument.versionLabel = null;

                axios.post('/rest/docs/update', vm.currentDocument)
                    .then(function (response) {
                        axios.get('rest/docs/' + vm.currentDocument.id + '/lastupdate')
                            .then(function (response) {
                                vm.currentDocument.lastModification = response.data;
                                vm.processStatus = vm.ProcessStatus.idle;
                            })
                            .catch(function (reason) {
                                vm.showError(reason);
                            })
                    })
                    .catch(function (reason) {
                        vm.showError(reason);
                    })
            }
        },
        authenticateThenSetup: function () {
            var vm = this;
            axios.get("/auth/me")
                .then(function (value) {
                    vm.username = value.data;
                    vm.setup();
                })
                .catch(function (reason) {
                    if(reason.response.status == 401) {
                        // unauthorized
                        window.location = "/login.html";
                    }
                });
        },
        setup: function () {
            var vm = this;

            vm.updateDocList();

            axios.get('/rest/push_interval')
                .then(function (value) {
                    vm.pushInterval = value.data;
                    setInterval(function () {
                        vm.pushChanges();
                    }, vm.pushInterval);
                })
                .catch(function (reason) {
                    vm.showError(reason);
                })

            axios.get('rest/fetch_interval')
                .then(function (value) {
                    vm.fetchInterval = value.data;
                    setInterval(function () {
                        vm.fetchChanges();
                    }, vm.fetchInterval);
                })
                .catch(function (reason) {
                    vm.showError(reason);
                })


        },
        logout: function () {
            axios.get('/logout')
                .then(function (value) {
                    window.location = "/login.html";
                })
                .catch(function (reason) {
                    alert(reason);
                })
        },
        updateActiveUsers: function () {
            var vm = this;
            axios.get('/rest/docs/' + vm.currentDocument.id + "/activeusers")
                .then(function (response) {
                    vm.activeUsers = response.data;
                })
                .catch(function (reason) {
                    vm.showError(reason);
                })
        }
    },
    created: function () {
        var vm = this;

        vm.authenticateThenSetup();
    }
});