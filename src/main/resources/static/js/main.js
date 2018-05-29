;
new Vue({
    el: "#main",
    data: {
        errorMessage: "",
        docs: {},
        documentName: "",
        docNameError: "",
        currentDocument: null,
        documentData: "",
        CommitStatus: {
            noData: 0,
            justLoaded: 1,
            modified: 2,
            pushing: 3,
            pushed: 4,
            fetching: 5,
            fetched: 6
        },
        commitStatus: 0,
        pushInterval: 3000,
        fetchInterval: 1000,
        username: ""
    },
    computed: {},
    watch: {
        documentData: function () {
            if(this.commitStatus != this.CommitStatus.justLoaded
                && this.commitStatus != this.CommitStatus.fetching) {
                this.commitStatus = this.CommitStatus.modified;
            } else {
                this.commitStatus = this.commitStatus == this.CommitStatus.fetching ?
                    this.CommitStatus.fetched : this.CommitStatus.pushed;
            }
        }
    },
    methods: {
        pushChanges: function () {
            if (this.currentDocument && this.commitStatus == this.CommitStatus.modified) {
                console.log("pushing!");
                this.pushDocument();
            }
        },
        fetchChanges: function () {
            if(this.currentDocument
                && this.checkFetchingAvailability()) {
                console.log("commit status: " + this.commitStatus);
                console.log("requesting last version");
                var vm = this;
                axios.get('/rest/docs/' + this.currentDocument.id + "/lastupdate")
                    .then(function (response) {
                        if(response.data > vm.currentDocument.lastModification
                            && vm.checkFetchingAvailability()) {
                            console.log("detected new version");
                            vm.commitStatus = vm.CommitStatus.fetching;
                            vm.loadDocument(vm.currentDocument.id, vm.CommitStatus.fetching);
                        }
                    })
                    .catch(function (reason) {
                        vm.showError(reason);
                    })
            }
        },
        checkFetchingAvailability: function () {
            return this.commitStatus == this.CommitStatus.pushed
                || this.commitStatus == this.CommitStatus.fetched;
        },
        showError: function (reason) {
            if(reason.response) {
                this.errorMessage = reason.response.data.message;
            } else {
                this.errorMessage = reason.message;
            }

            alert(this.errorMessage);
        },
        closeError: function() {
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
            axios.get("/rest/docs/" + id)
                .then(function (response) {
                    if(!commitStatus) {
                        commitStatus = vm.CommitStatus.justLoaded;
                    }
                    vm.commitStatus = commitStatus;
                    if(updateVersionOnly === true) {
                        vm.currentDocument.lastModified = response.data.lastModified;
                    } else {
                        vm.currentDocument = response.data;
                        vm.documentData = vm.currentDocument.data;
                    }
                })
                .catch(function (reason) {
                    vm.showError(reason);
                });
        },
        pushDocument: function () {
            if (this.currentDocument !== null) {
                var vm = this;
                vm.currentDocument.data = vm.documentData;
                vm.commitStatus = vm.CommitStatus.pushing;

                // we don't need to save document with old version labels
                // just leave it empty for now
                vm.currentDocument.versionLabel = null;

                axios.post('/rest/docs/update', vm.currentDocument)
                    .then(function (response) {
                        vm.loadDocument(vm.currentDocument.id, vm.CommitStatus.pushed, true);
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

            setInterval(function () {
                vm.pushChanges();
            }, vm.pushInterval);

            setInterval(function () {
                vm.fetchChanges();
            }, vm.fetchInterval);
        },
        logout: function () {
            axios.get('/logout')
                .then(function (value) {
                    window.location = "/login.html";
                })
                .catch(function (reason) {
                    alert(reason);
                })
        }
    },
    created: function () {
        var vm = this;

        vm.authenticateThenSetup();
    }
});