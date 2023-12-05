#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#
# Not all platforms are supported by Qemu. Using qemu-user therefore
# involves a certain risk, which is also the reason why this feature
# is not activated by default.

inherit qemu

CMAKE_EXEWRAPPER_ENABLED:class-native = "False"
CMAKE_EXEWRAPPER_ENABLED:class-nativesdk = "False"
CMAKE_EXEWRAPPER_ENABLED ?= "${@bb.utils.contains('MACHINE_FEATURES', 'qemu-usermode', 'True', 'False', d)}"
DEPENDS:append = "${@' qemu-native' if d.getVar('CMAKE_EXEWRAPPER_ENABLED') == 'True' else ''}"

cmake_do_generate_toolchain_file:append:class-target() {
    if [ "${CMAKE_EXEWRAPPER_ENABLED}" = "True" ]; then
        # Write out a qemu wrapper that will be used as exe_wrapper so that camake
        # can run target helper binaries through that. This also allows to execute ctest.
        qemu_binary="${@qemu_wrapper_cmdline(d, '${STAGING_DIR_HOST}', ['${STAGING_DIR_HOST}/${libdir}','${STAGING_DIR_HOST}/${base_libdir}'])}"
        echo "#!/bin/sh" > "${WORKDIR}/cmake-qemuwrapper"
        echo "$qemu_binary \"\$@\"" >> "${WORKDIR}/cmake-qemuwrapper"
        chmod +x "${WORKDIR}/cmake-qemuwrapper"
        echo "set( CMAKE_CROSSCOMPILING_EMULATOR ${WORKDIR}/cmake-qemuwrapper)" \
          >> ${WORKDIR}/toolchain.cmake
    fi
}
