;
new Vue({
    el: "#main",
    data: {
        message: "Hello there, you are about to use CoEditor!",
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
            pushed: 4
        },
        commitStatus: 0,
        lastEdit: null,
        pushInterval: 3000
    },
    computed: {},
    watch: {
        documentData: function () {
            if(this.commitStatus != this.CommitStatus.justLoaded) {
                this.commitStatus = this.CommitStatus.modified;
            } else {
                this.commitStatus = this.CommitStatus.pushed;
            }
        }
    },
    methods: {
        pushChanges: function () {
            if (this.currentDocument && this.commitStatus == this.CommitStatus.modified) {
                this.commitStatus = this.CommitStatus.pushing;
                this.pushDocument();
            }
        },
        showError: function (reason) {
            this.message = reason.response.data.message;
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
                    vm.lastEdit = vm.currentDocument.lastModification;
                })
                .catch(function (reason) {
                    vm.showError(reason);
                });
        },
        pushDocument: function () {
            if (this.currentDocument !== null) {
                var vm = this;
                vm.currentDocument.data = vm.documentData;
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
    }
});