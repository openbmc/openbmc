#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

project('meson-example', 'cpp',
    version: '1.0.0',
    default_options: ['cpp_std=c++17']
    )

jsoncdep = dependency('json-c')

if get_option('FAILING_TEST').enabled()
    add_project_arguments('-DFAIL_COMPARISON_STR=foo', language: 'cpp')
endif

mesonexlib = shared_library('mesonexlib',
    'cpp-example-lib.cpp', 'cpp-example-lib.hpp',
	version: meson.project_version(),
	soversion: meson.project_version().split('.')[0],
    dependencies : jsoncdep,
    install : true
    )

executable('mesonex',
    'cpp-example.cpp',
    link_with : mesonexlib,
    install : true
    )

test_mesonex = executable('test-mesonex',
    'test-cpp-example.cpp',
    link_with : mesonexlib,
    install : true
)

test('meson example test', test_mesonex)
