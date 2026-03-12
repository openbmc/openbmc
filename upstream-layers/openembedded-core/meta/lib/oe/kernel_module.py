#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

# Set up the environment for building kernel modules
def kernel_module_os_env(d, env_dict):
    env_dict['CFLAGS'] = ''
    env_dict['CPPFLAGS'] = ''
    env_dict['CXXFLAGS'] = ''
    env_dict['LDFLAGS'] = ''

    env_dict['KERNEL_PATH'] = d.getVar('STAGING_KERNEL_DIR')
    env_dict['KERNEL_SRC'] = d.getVar('STAGING_KERNEL_DIR')
    env_dict['KERNEL_VERSION'] = d.getVar('KERNEL_VERSION')
    env_dict['CC'] = d.getVar('KERNEL_CC')
    env_dict['LD'] = d.getVar('KERNEL_LD')
    env_dict['AR'] = d.getVar('KERNEL_AR')
    env_dict['OBJCOPY'] = d.getVar('KERNEL_OBJCOPY')
    env_dict['STRIP'] = d.getVar('KERNEL_STRIP')
    env_dict['O'] = d.getVar('STAGING_KERNEL_BUILDDIR')
    kbuild_extra_symbols = d.getVar('KBUILD_EXTRA_SYMBOLS')
    if kbuild_extra_symbols:
        env_dict['KBUILD_EXTRA_SYMBOLS'] = kbuild_extra_symbols
    else:
        env_dict['KBUILD_EXTRA_SYMBOLS'] = ''
