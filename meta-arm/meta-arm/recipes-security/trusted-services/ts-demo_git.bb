DESCRIPTION = "Trusted Services ts-demo deployment for arm-linux. \
               Used for running simple TS demo from Linux user-space \
               on an Arm platform with real deployments of trusted services."

TS_ENV = "arm-linux"

require trusted-services.inc

DEPENDS        += "python3-jsonschema-native python3-jinja2-native"
DEPENDS        += "libts"
RDEPENDS:${PN} += "libts"

OECMAKE_SOURCEPATH="${S}/deployments/ts-demo/${TS_ENV}"

# Mbedtls 3.1.0 does not compile with clang.
# This can be removed after TS updated required mbedtls version
TOOLCHAIN = "gcc"

FILES:${PN} = "${bindir}/ts-demo"

# TODO: remove FORTIFY_SOURCE as MbedTLS fails to build in yocto if this
# compilation flag is used.
lcl_maybe_fortify = "${@oe.utils.conditional('OPTLEVEL','-O0','','${OPTLEVEL}',d)}"

do_install:append () {
    install -d ${D}${bindir}
    mv ${D}${TS_INSTALL}/bin/ts-demo ${D}${bindir}

    rm -r --one-file-system ${D}${TS_INSTALL}
}
