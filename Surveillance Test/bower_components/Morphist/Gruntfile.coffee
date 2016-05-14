module.exports = (grunt) ->
    grunt.initConfig
        pkg: grunt.file.readJSON 'package.json'

        uglify:
            options:
                banner: '/*! <%= pkg.name %> - v<%= pkg.version %> - ' +
                        '<%= grunt.template.today("yyyy-mm-dd") %> */'
            dist:
                files:
                    'dist/morphist.min.js': ['dist/morphist.js']

        eslint:
            all: ['dist/morphist.js']

    grunt.loadNpmTasks 'grunt-contrib-uglify'
    grunt.loadNpmTasks 'grunt-eslint'

    grunt.registerTask 'default', ['test', 'uglify']
    grunt.registerTask 'test', ['eslint']