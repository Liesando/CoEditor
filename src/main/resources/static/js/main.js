;
new Vue({
    el: "#main",
    data: {
        isPageLoading: true,
        errorMessage: "",
        hasErrors: false,
        docs: {},
        documentName: "",
        versionLabel: "",
        docNameError: "",
        currentDocument: null,
        docVersions: null,
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
        observingOldVersion: false,
        labellingMode: false,
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
        },
        versionLabel: function (newValue) {
            this.versionLabel = newValue.replace('?', '');
        },
        errorMessage: function (newValue) {
            this.hasErrors = newValue.trim().length > 0;
        }
    },
    methods: {
        pushChanges: function () {
            if (!this.observingOldVersion && this.currentDocument
                && this.commitStatus == this.CommitStatus.modified
                && this.processStatus == this.ProcessStatus.idle) {
                console.log("pushing!");
                this.pushDocument();
            }
        },
        fetchChanges: function () {

            // always
            this.updateDocList();

            if (this.currentDocument) {
                this.updateActiveUsers();
                this.updateDocumentVersions();
            }

            if (!this.observingOldVersion
                && this.currentDocument
                && this.checkFetchingAvailability()) {
                console.log("requesting last version");
                var vm = this;
                axios.get('/rest/docs/' + this.currentDocument.primaryKey.documentId + "/lastupdate")
                    .then(function (response) {
                        if (response.data > vm.currentDocument.primaryKey.modificationTime
                            && vm.checkFetchingAvailability()) {
                            console.log("detected new version");
                            vm.commitStatus = vm.CommitStatus.fetched;
                            vm.loadDocument(vm.currentDocument.primaryKey.documentId, vm.CommitStatus.fetched);
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
        },
        createDocument: function () {
            var vm = this;
            if (this.documentName.length > 0) {
                axios.post('/rest/docs', {
                    id: 0,
                    name: vm.documentName
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
        loadDocument: function (id, commitStatus) {
            var vm = this;
            vm.processStatus = vm.ProcessStatus.fetching;
            vm.observingOldVersion = false;
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
        loadLabelledDocument: function (id, version) {
            var vm = this;
            vm.processStatus = vm.ProcessStatus.fetching;
            axios.get('/rest/docs/' + id + "/version/" + version)
                .then(function (response) {
                    vm.ignoreWatchOnce = true;
                    vm.currentDocument = response.data;
                    vm.documentData = vm.currentDocument.data;

                    vm.commitStatus = vm.CommitStatus.justLoaded;
                    vm.processStatus = vm.ProcessStatus.idle;
                    vm.observingOldVersion = true;
                })
                .catch(function (reason) {
                    vm.showError(reason);
                })
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

                axios.put('/rest/docs', vm.currentDocument)
                    .then(function (response) {
                        axios.get('rest/docs/' + vm.currentDocument.primaryKey.documentId + '/lastupdate')
                            .then(function (response) {
                                vm.currentDocument.primaryKey.modificationTime = response.data;
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
        pushDocumentVersion: function () {
            if (this.currentDocument !== null) {
                var vm = this;

                vm.currentDocument.data = vm.documentData;
                vm.currentDocument.versionLabel = vm.versionLabel;
                vm.versionLabel = "";
                vm.labellingMode = false;

                axios.patch('/rest/docs', vm.currentDocument)
                    .catch(function (reason) {
                        vm.showError(reason);
                    })
            }
        },
        updateDocumentVersions: function () {
            if (this.currentDocument !== null) {
                var vm = this;
                axios.get('/rest/docs/' + vm.currentDocument.primaryKey.documentId + "/version/all")
                    .then(function (response) {
                        vm.docVersions = response.data;
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
                    vm.isPageLoading = false;
                })
                .catch(function (reason) {
                    if (reason.response.status == 401) {
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
            axios.get('/rest/docs/' + vm.currentDocument.primaryKey.documentId + "/activeusers")
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