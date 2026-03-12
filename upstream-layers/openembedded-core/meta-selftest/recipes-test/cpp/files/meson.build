#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

project('meson-example', 'cpp',
    version: '1.0.0',
    default_options: ['cpp_std=c++17'],
    meson_version: '>=1.1.0'
    )

jsoncdep = dependency('json-c')

if get_option('FAILING_TEST').enabled()
    add_project_arguments('-DFAIL_COMPARISON_STR=foo', language: 'cpp')
endif

# Generate config.h from config.h.in
config_path = get_option('sysconfdir') / 'meson-example.conf'
conf_data = configuration_data()
conf_data.set('CPP_EXAMPLE_CONFIG_PATH', config_path)
configure_file(input : 'config.h.in',
               output : 'config.h',
               configuration : conf_data)

# Include the build directory for config.h
inc_dir = include_directories('.')

mesonexlib = shared_library('mesonexlib',
    'cpp-example-lib.cpp', 'cpp-example-lib.hpp',
    version: meson.project_version(),
    soversion: meson.project_version().split('.')[0],
    dependencies : jsoncdep,
    include_directories : inc_dir,
    install : true
    )

executable('mesonex',
    'cpp-example.cpp',
    link_with : mesonexlib,
    include_directories : inc_dir,
    install : true
    )

test_mesonex = executable('test-mesonex',
    'test-cpp-example.cpp',
    link_with : mesonexlib,
    include_directories : inc_dir,
    install : true
)

test('meson example test', test_mesonex)
