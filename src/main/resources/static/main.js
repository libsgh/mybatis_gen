Vue.config.productionTip = false;
Vuetify.config.silent = true;
const default_config = {
    gen_type: 'MyBatis3',
    is_gen_example: 'N',
    is_jsr310: 'N',
    plugin:["note"],
    default_model_package_name:"com.gen.model",
    default_mapper_package_name:"com.gen.mapper",
    ignore_prefix:"",
    sql:"",
};
const default_content = {
    java_model:'',
    java_example:'',
    java_mapper:'',
    xml_mapper:'',
    dynamic_sql:'',
}
new Vue({
    el: '#app',
    vuetify: new Vuetify(),
    filters: {
        sizeF: function (value) {
            let fileSizeByte = value;
            let fileSizeMsg = "";
            if (fileSizeByte < 1048576) fileSizeMsg = (fileSizeByte / 1024).toFixed(2) + " KB";
            else if (fileSizeByte == 1048576) fileSizeMsg = "1MB";
            else if (fileSizeByte > 1048576 && fileSizeByte < 1073741824) fileSizeMsg = (fileSizeByte / (1024 * 1024)).toFixed(2) + " MB";
            else if (fileSizeByte > 1048576 && fileSizeByte == 1073741824) fileSizeMsg = "1GB";
            else if (fileSizeByte > 1073741824 && fileSizeByte < 1099511627776) fileSizeMsg = (fileSizeByte / (1024 * 1024 * 1024)).toFixed(2) + " GB";
            else fileSizeMsg = "文件超过1TB";
            return fileSizeMsg;
        },
    },
    data: {
        gen_config : default_config,
        items: [
            '配置', '预览', '文件下载'
        ],
        tab:null,
        loading1: false,
        loading2: false,
        loader: null,
        snackbar: false,
        showExample:true,
        msg: ``,
        content: default_content,
        files: [],
        overlay: false,
    },
    methods: {
        async reset(){
            localStorage.removeItem('gen_config_history');
            localStorage.removeItem('gen_content');
            localStorage.removeItem('gen_files');
            this.content = default_content;
            this.gen_config = default_config;
            this.files = [];
        },
        async loadHistory () {
            const config_history = localStorage.getItem('gen_config_history');
            if(config_history != null){
                this.gen_config = JSON.parse(config_history);
            }else{
                this.gen_config = default_config;
            }
            const content_history = localStorage.getItem('gen_content');
            if(content_history != null){
                this.content = JSON.parse(content_history);
            }else{
                this.content = default_content;
            }
            const files_history = localStorage.getItem('gen_files');
            if(files_history != null){
                this.files = JSON.parse(files_history);
            }else{
                this.files = [];
            }
        },
        async gen (action, event) {
            this.overlay = true;
            if(action == 'download'){
                this.loading2 = true;
            }else{
                this.loading1 = true;
            }
            event.preventDefault();
            await axios
                .post('/api/gen', JSON.stringify(this.gen_config), {headers: {
                'Content-Type': 'application/json; charset=utf-8'
            }}).then(response => {
                localStorage.setItem('gen_config_history', JSON.stringify(this.gen_config));
                if(action == 'download'){
                    this.loading2 = false;
                }else{
                    this.loading1 = false;
                }
                if(response.data.code < 0){
                    this.msg = response.data.msg;
                    this.snackbar = true;
                }else{
                    //success
                    this.tab = action;
                    this.content = response.data.data.content;
                    this.files = response.data.data.files;
                    localStorage.setItem('gen_content', JSON.stringify(this.content));
                    localStorage.setItem('gen_files', JSON.stringify(this.files));
                }
                this.overlay = false;
            })
        },
        getPrefix(){
            if(this.gen_config.sql != ''){
                let formdata = new FormData();
                formdata.append('sql', this.gen_config.sql);
                axios.post('/api/prefix', formdata, {headers: {
                            'Content-Type': 'multipart/form-data'
                        }}).then(response => {
                    if(response.data.code < 0){
                        this.msg = response.data.msg;
                        this.snackbar = true;
                    }else{
                        if(response.data.data != ""){
                            this.gen_config.ignore_prefix = response.data.data;
                        }
                    }
                })
            }
        },
        onCopy: function (e) {
            this.msg = '内容已复制到剪切板';
            this.snackbar = true;
        },
        onError: function (e) {
            console.error("Copy error!", e);
        },
    },
    watch: {
        loader () {
            const l = this.loader
            this[l] = !this[l]

            setTimeout(() => (this[l] = false), 3000)

            this.loader = null
        },
    },
    mounted () {
        this.loadHistory();
    },
});