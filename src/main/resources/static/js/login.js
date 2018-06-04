;
new Vue({
    el: "#main",
    data: {
        loginMode: true,
        username: "",
        password: "",
        errorMessage: "",
        hasErrors: false
    },
    watch: {
        errorMessage: function (newValue) {
            this.hasErrors = newValue.trim().length > 0;
        }
    },
    methods: {
        switchMode: function () {
            this.loginMode = !this.loginMode;
        },
        showError: function (message) {
            this.errorMessage = message;
        },
        validateData: function () {
            return this.username.trim().length > 0
                && this.password.length > 0;
        },
        register: function () {
            if(!this.validateData()) {
                this.errorMessage = "Username and password must not be empty!";
                return;
            }
            var vm = this;
            if (this.username.length > 0
                && this.password.length > 0) {
                axios.post('/auth/register', {
                    username: this.username,
                    password: this.password
                })
                    .then(function (value) {
                        vm.doLogin();
                    })
                    .catch(function (reason) {
                        if (reason.response.status == 500) {
                            vm.showError("username already exists");
                        } else {
                            vm.showError(reason.response.data.message);
                        }
                    })
            }
        },
        doLogin: function () {
            if (!this.validateData()) {
                this.errorMessage = "Username and password must not be empty!";
                return;
            }
            var vm = this;
            axios.post('/login', "username=" + this.username + "&password=" + this.password)
                .then(function (value) {
                    window.location = "/";
                })
                .catch(function (reason) {
                    if(reason.response.status == 401) {
                        vm.showError("wrong credentials!");
                    } else {
                        vm.showError("server error");
                    }
                });
        }
    }
});