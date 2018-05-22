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
        fetchInterval: 1000
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
                this.commitStatus = this.CommitStatus.pushing;
                this.pushDocument();
            }
        },
        fetchChanges: function () {
            if(this.currentDocument) {
                var vm = this;
                axios.get('/rest/docs/last/' + this.currentDocument.id)
                    .then(function (response) {
                        if(response.data > vm.currentDocument.lastModification) {
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
        loadDocument: function (id, commitStatus) {
            var vm = this;
            axios.get("/rest/docs/" + id)
                .then(function (response) {
                    if(!commitStatus) {
                        commitStatus = vm.CommitStatus.justLoaded;
                    }
                    vm.commitStatus = commitStatus;
                    vm.currentDocument = response.data;
                    vm.documentData = vm.currentDocument.data;
                })
                .catch(function (reason) {
                    vm.showError(reason);
                });
        },
        pushDocument: function () {
            if (this.currentDocument !== null) {
                var vm = this;
                vm.currentDocument.data = vm.documentData;

                // we don't need to save this with old labels
                // just leave it empty for now
                vm.currentDocument.versionLabel = null;

                axios.post('/rest/docs/update', vm.currentDocument)
                    .then(function (response) {
                        vm.loadDocument(vm.currentDocument.id, vm.CommitStatus.pushed);
                    })
                    .catch(function (reason) {
                        vm.showError(reason);
                    })
            }
        }
    },
    created: function () {
        var vm = this;
        vm.updateDocList();

        setInterval(function () {
            vm.pushChanges();
        }, vm.pushInterval);

        setInterval(function () {
            vm.fetchChanges();
        }, vm.fetchInterval);
    }
});