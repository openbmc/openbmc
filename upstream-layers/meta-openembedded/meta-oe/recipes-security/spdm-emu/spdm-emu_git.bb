SUMMARY = "SPDM Emulator"
DESCRIPTION = "Sample SPDM emulator implementation using libspdm \
for testing SPDM communication."
HOMEPAGE = "https://github.com/DMTF/spdm-emu"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=ed4cfe4688e1ac2cc2e6748766571949"

SRC_URI = "gitsm://github.com/DMTF/spdm-emu;protocol=https;branch=main"

SRCREV = "5fca359b15e4edd4f070ca83c9da02799b6f78ae"
inherit cmake pkgconfig systemd

DEPENDS = "openssl"
PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[systemd] = "-DENABLE_SYSTEMD=ON,-DENABLE_SYSTEMD=OFF,systemd"

SYSTEMD_SERVICE:${PN} = "spdm-responder-emu.service"

def get_spdm_multiarch(d):
    target_arch = d.getVar('TARGET_ARCH')
    multiarch_options = {
        "x86_64":  "x64",
        "i586":    "ia32",
        "i686":    "ia32",
        "arm":     "arm",
        "aarch64": "aarch64",
        "riscv32": "riscv32",
        "riscv64": "riscv64",
        "ppc64le": "ppc64le",
        "powerpc64le": "ppc64le",
    }

    if target_arch in multiarch_options:
        return multiarch_options[target_arch]

    bb.fatal("unsupported architecture '%s'" % target_arch)

EXTRA_OECMAKE += "\
    -DARCH=${@get_spdm_multiarch(d)} \
    -DTOOLCHAIN=NONE \
    -DTARGET=Release \
    -DCRYPTO=openssl \
    -DENABLE_BINARY_BUILD=1 \
    -DCOMPILED_LIBCRYPTO_PATH=${libdir} \
    -DCOMPILED_LIBSSL_PATH=${libdir} \
"

OECMAKE_TARGET_COMPILE += " copy_sample_key"

do_install:append() {
    # Install binaries to /usr/bin
    install -d ${D}${bindir}
    install -m 0755 ${B}/bin/spdm_requester_emu ${D}${bindir}/
    install -m 0755 ${B}/bin/spdm_responder_emu ${D}${bindir}/
    install -m 0755 ${B}/bin/spdm_device_attester_sample ${D}${bindir}/
    install -m 0755 ${B}/bin/spdm_device_validator_sample ${D}${bindir}/

    # Install certificates/keys to /usr/share/spdm-emu
    install -d ${D}${datadir}/spdm-emu
    for f in ${B}/bin/*; do
        case "$(basename $f)" in
            spdm_requester_emu|spdm_responder_emu|spdm_device_attester_sample|spdm_device_validator_sample)
                ;;
            *)
                if [ -d "$f" ]; then
                    cp -r --no-preserve=ownership $f ${D}${datadir}/spdm-emu/
                else
                    install -m 0644 $f ${D}${datadir}/spdm-emu/
                fi
                ;;
        esac
    done
}

FILES:${PN} += "${datadir}/spdm-emu"
