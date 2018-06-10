var editor = {

    props: {value: null, config: null, disabled: Boolean},

    watch: {
        // changed from outside
        value: function () {
            this.model = this.value;

            if (this.oldModel != this.model) {
                this.setContent();
            }
        },
        disabled: function (val, oldVal) {
            if (!oldVal && val) {
                this._$element.froalaEditor('edit.off');
            } else if (oldVal & !val) {
                this._$element.froalaEditor('edit.on');
            }
        }
    },

    template: '<textarea :value="value"></textarea>',

    mounted: function () {
        // fetch initial value into model
        this.model = this.value;
        console.log(this.model);
        this.createEditor();
    },

    data: function () {
        return {

            // Jquery wrapped element.
            _$element: null,

            model: null,

            oldModel: null
        };
    },

    methods: {
        createEditor: function () {
            var self = this;

            this._$element = $(this.$el);

            this.oldModel = this.model;

            this.setContent();
            this.registerEvent(this._$element, 'froalaEditor.initialized', function () {
                self.setContent();
            });
            this._$element.froalaEditor({height: '60vh', placeholderText: ''});
            if (this.disabled) {
                this._$element.froalaEditor('edit.off');
            }
            this.initListeners();

            console.log('editor created');
        },

        setContent: function () {
            this._$element.froalaEditor('html.set', this.model ? this.model : '', true);
            this.oldModel = this.model;
        },

        updateModel: function () {
            var modelContent = '';
            var returnedHtml = this._$element.froalaEditor('html.get', true);
            if (typeof returnedHtml === 'string') {
                modelContent = returnedHtml;
            }

            this.model = modelContent;
            this.oldModel = modelContent;
            this.$emit('input', modelContent);
        },

        initListeners: function () {
            var self = this;

            this.registerEvent(this._$element, 'froalaEditor.contentChanged', function () {
                self.updateModel();
            });
        },

        registerEvent: function (elem, event, callback) {

            if (!elem || !event || !callback) {
                return;
            }

            // this.listeningEvents.push(eventName);
            elem.on(event, callback);
        }
    }
};

Vue.component('editor', editor);