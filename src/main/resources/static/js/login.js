;
new Vue({
    el: "#main",
    data: {
        loginMode: true,
        username: "",
        password: ""
    },
    methods: {
        switchMode: function () {
            this.loginMode = !this.loginMode;
        },
        register: function () {
            var vm = this;
            if(this.username.length > 0
                && this.password.length > 0) {
                axios.post('/auth/register', {
                    username: this.username,
                    password: this.password
                })
                    .then(function (value) {
                        vm.doLogin();
                    })
                    .catch(function (reason) {
                        if(reason.response.status == 500) {
                            alert("username already exists");
                        } else {
                            alert(reason.response.data.message);
                        }
                    })
            }
        },
        doLogin: function () {
            axios.post('/login', "username=" + this.username + "&password=" + this.password)
                .then(function (value) {
                    window.location = "/";
                })
                .catch(function (reason) {
                    alert('wrong credentials!\n' + reason.response.data.message);
                });
        }
    }
});