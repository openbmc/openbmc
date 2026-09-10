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

binary_name = get_option('BINARY_NAME')
test_binary_name = get_option('TEST_BINARY_NAME')

# Generate config.h from config.h.in
config_path = get_option('sysconfdir') / get_option('CONFIG_FILE_NAME')
conf_data = configuration_data()
conf_data.set('CPP_EXAMPLE_CONFIG_PATH', config_path)
configure_file(input : 'config.h.in',
               output : 'config.h',
               configuration : conf_data)

# Include the build directory for config.h
inc_dir = include_directories('.')

example_sources = ['cpp-example.cpp']
if get_option('WITH_SYSTEMD').enabled()
    add_project_arguments('-DWITH_SYSTEMD', language: 'cpp')
else
    example_sources += ['daemonize.cpp', 'daemonize.hpp']
endif

exlib = shared_library(binary_name + 'lib',
    'cpp-example-lib.cpp', 'cpp-example-lib.hpp',
    version: meson.project_version(),
    soversion: meson.project_version().split('.')[0],
    dependencies : jsoncdep,
    include_directories : inc_dir,
    install : true
    )

executable(binary_name,
    example_sources,
    link_with : exlib,
    include_directories : inc_dir,
    install : true
    )

test_exe = executable(test_binary_name,
    'test-cpp-example.cpp',
    link_with : exlib,
    include_directories : inc_dir,
    install : true
)

test('meson example test', test_exe)
