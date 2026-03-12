#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#
project('which', 'c', version: '2.23')

add_project_arguments('-DHAVE_CONFIG_H', language : 'c')

src = ['bash.c','getopt1.c','getopt.c','tilde.c','which.c']

executable('which', src, install: true)
install_man('which.1')

conf_data = configuration_data()

conf_data.set('GETGROUPS_T', 'gid_t')

compiler = meson.get_compiler('c')
if compiler.has_function('strchr')
    conf_data.set('HAVE_STRCHR', 1)
endif
if compiler.has_function('memcpy')
    conf_data.set('HAVE_MEMCPY', 1)
endif
if compiler.has_function('getopt_long')
    conf_data.set('HAVE_GETOPT_LONG', 1)
endif
if compiler.has_function('getcwd')
    conf_data.set('HAVE_GETCWD', 1)
endif
if compiler.has_function('getwd')
    conf_data.set('HAVE_GETWD', 1)
endif
if compiler.has_function('sysconf')
    conf_data.set('HAVE_SYSCONF', 1)
endif
if compiler.has_function('getgroups')
    conf_data.set('HAVE_GETGROUPS', 1)
endif
if compiler.has_function('eaccess')
    conf_data.set('HAVE_EACCESS', 1)
endif
if compiler.has_function('getpwent')
    conf_data.set('HAVE_GETPWENT', 1)
endif
if compiler.has_function('getpwnam')
    conf_data.set('HAVE_GETPWNAM', 1)
endif
if compiler.has_function('getpwuid')
    conf_data.set('HAVE_GETPWUID', 1)
    conf_data.set('HAVE_GETPW_DECLS', 1)
endif

if compiler.has_header('unistd.h')
    conf_data.set('HAVE_UNISTD_H', 1)
endif
if compiler.has_header('string.h')
    conf_data.set('HAVE_STRING_H', 1)
endif
if compiler.has_header('stdlib.h')
    conf_data.set('HAVE_STDLIB_H', 1)
endif
if compiler.has_header('memory.h')
    conf_data.set('HAVE_MEMORY_H', 1)
endif
if compiler.has_header('pwd.h')
    conf_data.set('HAVE_PWD_H', 1)
endif
if compiler.has_header('strings.h')
    conf_data.set('HAVE_STRINGS_H', 1)
endif
if compiler.has_header('inttypes.h')
    conf_data.set('HAVE_INTTYPES_H', 1)
endif
if compiler.has_header('stdio.h')
    conf_data.set('HAVE_STDIO_H', 1)
endif
if compiler.has_header('stdint.h')
    conf_data.set('HAVE_STDINT_H', 1)
endif
if compiler.has_header('sys/stat.h')
    conf_data.set('HAVE_SYS_STAT_H', 1)
endif
if compiler.has_header('sys/types.h')
    conf_data.set('HAVE_SYS_TYPES_H', 1)
endif
conf_data.set('NEED_XMALLOC', 1)
conf_data.set('STDC_HEADERS', 1)
conf_data.set('VERSION', '"' + meson.project_version() + '"')

configure_file(output: 'config.h', configuration: conf_data)
