SUMMARY = "A simple daemon to allow session software to update firmware"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "glib-2.0 libxmlb json-glib sqlite3 libjcat gcab vala-native"

SRC_URI = "https://github.com/${BPN}/${BPN}/releases/download/${PV}/${BP}.tar.xz \
           file://run-ptest"
SRC_URI[sha256sum] = "7ebe1514acb688d2649cecc392db929094da3f03cfc98a9c0faccabb084912fa"

UPSTREAM_CHECK_URI = "https://github.com/${BPN}/${BPN}/releases"

# Machine-specific as we examine MACHINE_FEATURES to decide whether to build the UEFI plugins
PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit meson vala gobject-introspection systemd bash-completion pkgconfig gi-docgen ptest manpages

GIDOCGEN_MESON_OPTION = 'docs'
GIDOCGEN_MESON_ENABLE_FLAG = 'docgen'
GIDOCGEN_MESON_DISABLE_FLAG = 'none'

PACKAGECONFIG ??= "curl gnutls gudev gusb \
                   ${@bb.utils.filter('DISTRO_FEATURES', 'bluetooth polkit', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd offline', '', d)} \
                   ${@bb.utils.contains('MACHINE_FEATURES', 'efi', 'plugin_uefi_capsule plugin_uefi_pk', '', d)} \
                   ${@bb.utils.contains('PTEST_ENABLED', '1', 'tests plugin_dummy', '', d)} \
                   hsi \
                   plugin_acpi_phat \
                   plugin_amt \
                   plugin_bcm57xx \
                   plugin_emmc \
                   plugin_ep963x \
                   plugin_fastboot \
                   plugin_flashrom \
                   plugin_gpio \
                   plugin_intel_spi \
                   plugin_logitech_bulkcontroller \
                   plugin_modem_manager \
                   plugin_msr \
                   plugin_nitrokey \
                   plugin_nvme \
                   plugin_parade_lspcon \
                   plugin_pixart_rf \
                   plugin_platform_integrity \
                   plugin_realtek_mst \
                   plugin_redfish \
                   plugin_synaptics_mst \
                   plugin_synaptics_rmi \
                   plugin_scsi \
                   plugin_thunderbolt \
                   plugin_uf2 \
                   plugin_upower \
                   sqlite"

PACKAGECONFIG[bluetooth] = "-Dbluez=true,-Dbluez=false"
PACKAGECONFIG[compat-cli] = "-Dcompat_cli=true,-Dcompat_cli=false"
PACKAGECONFIG[consolekit] = "-Dconsolekit=true,-Dconsolekit=false,consolekit"
PACKAGECONFIG[curl] = "-Dcurl=true,-Dcurl=false,curl"
PACKAGECONFIG[firmware-packager] = "-Dfirmware-packager=true,-Dfirmware-packager=false"
PACKAGECONFIG[fish-completion] = "-Dfish_completion=true,-Dfish_completion=false"
PACKAGECONFIG[gnutls] = "-Dgnutls=true,-Dgnutls=false,gnutls"
PACKAGECONFIG[gudev] = "-Dgudev=true,-Dgudev=false,libgudev"
PACKAGECONFIG[gusb] = "-Dgusb=true,-Dgusb=false,libgusb"
PACKAGECONFIG[hsi] = "-Dhsi=true,-Dhsi=false"
PACKAGECONFIG[libarchive] = "-Dlibarchive=true,-Dlibarchive=false,libarchive"
PACKAGECONFIG[manpages] = "-Dman=true,-Dman=false"
PACKAGECONFIG[metainfo] = "-Dmetainfo=true,-Dmetainfo=false"
PACKAGECONFIG[offline] = "-Doffline=true,-Doffline=false"
PACKAGECONFIG[polkit] = "-Dpolkit=true,-Dpolkit=false,polkit"
PACKAGECONFIG[sqlite] = "-Dsqlite=true,-Dsqlite=false,sqlite3"
PACKAGECONFIG[systemd] = "-Dsystemd=true,-Dsystemd=false,systemd"
PACKAGECONFIG[tests] = "-Dtests=true,-Dtests=false,gcab-native"


# TODO plugins-all meta-option that expands to all plugin_*?
PACKAGECONFIG[plugin_acpi_phat] = "-Dplugin_acpi_phat=true,-Dplugin_acpi_phat=false"
PACKAGECONFIG[plugin_amt] = "-Dplugin_amt=true,-Dplugin_amt=false"
PACKAGECONFIG[plugin_bcm57xx] = "-Dplugin_bcm57xx=true,-Dplugin_bcm57xx=false"
PACKAGECONFIG[plugin_cfu] = "-Dplugin_cfu=true,-Dplugin_cfu=false"
PACKAGECONFIG[plugin_dell] = "-Dplugin_dell=true,-Dplugin_dell=false,libsmbios"
PACKAGECONFIG[plugin_dummy] = "-Dplugin_dummy=true,-Dplugin_dummy=false"
PACKAGECONFIG[plugin_emmc] = "-Dplugin_emmc=true,-Dplugin_emmc=false"
PACKAGECONFIG[plugin_ep963x] = "-Dplugin_ep963x=true,-Dplugin_ep963x=false"
PACKAGECONFIG[plugin_fastboot] = "-Dplugin_fastboot=true,-Dplugin_fastboot=false"
PACKAGECONFIG[plugin_flashrom] = "-Dplugin_flashrom=true,-Dplugin_flashrom=false,flashrom"
PACKAGECONFIG[plugin_gpio] = "-Dplugin_gpio=true,-Dplugin_gpio"
PACKAGECONFIG[plugin_intel_spi] = "-Dplugin_intel_spi=true -Dlzma=true,-Dplugin_intel_spi=false -Dlzma=false,xz"
PACKAGECONFIG[plugin_logitech_bulkcontroller] = "-Dplugin_logitech_bulkcontroller=true,-Dplugin_logitech_bulkcontroller=false,protobuf-c-native protobuf-c"
PACKAGECONFIG[plugin_modem_manager] = "-Dplugin_modem_manager=true,-Dplugin_modem_manager=false,libqmi modemmanager"
PACKAGECONFIG[plugin_msr] = "-Dplugin_msr=true,-Dplugin_msr=false,cpuid"
PACKAGECONFIG[plugin_nitrokey] = "-Dplugin_nitrokey=true,-Dplugin_nitrokey=false"
PACKAGECONFIG[plugin_nvme] = "-Dplugin_nvme=true,-Dplugin_nvme=false"
PACKAGECONFIG[plugin_parade_lspcon] = "-Dplugin_parade_lspcon=true,-Dplugin_parade_lspcon=false"
PACKAGECONFIG[plugin_pixart_rf] = "-Dplugin_pixart_rf=true,-Dplugin_pixart_rf=false"
PACKAGECONFIG[plugin_platform_integrity] = "-Dplugin_platform_integrity=true,-Dplugin_platform_integrity=false"
PACKAGECONFIG[plugin_powerd] = "-Dplugin_powerd=true,-Dplugin_powerd=false"
PACKAGECONFIG[plugin_realtek_mst] = "-Dplugin_realtek_mst=true,-Dplugin_realtek_mst=false"
PACKAGECONFIG[plugin_redfish] = "-Dplugin_redfish=true,-Dplugin_redfish=false"
PACKAGECONFIG[plugin_scsi] = "-Dplugin_scsi=true,-Dplugin_scsi=false"
PACKAGECONFIG[plugin_synaptics_mst] = "-Dplugin_synaptics_mst=true,-Dplugin_synaptics_mst=false"
PACKAGECONFIG[plugin_synaptics_rmi] = "-Dplugin_synaptics_rmi=true,-Dplugin_synaptics_rmi=false"
PACKAGECONFIG[plugin_thunderbolt] = "-Dplugin_thunderbolt=true,-Dplugin_thunderbolt=false"
PACKAGECONFIG[plugin_tpm] = "-Dplugin_tpm=true,-Dplugin_tpm=false,tpm2-tss"
# Turn off the capsule splash as it needs G-I at buildtime, which isn't currently supported
PACKAGECONFIG[plugin_uefi_capsule] = "-Dplugin_uefi_capsule=true -Dplugin_uefi_capsule_splash=false,-Dplugin_uefi_capsule=false,efivar fwupd-efi"
PACKAGECONFIG[plugin_uefi_pk] = "-Dplugin_uefi_pk=true,-Dplugin_uefi_pk=false"
PACKAGECONFIG[plugin_uf2] = "-Dplugin_uf2=true,-Dplugin_uf2=false"
PACKAGECONFIG[plugin_upower] = "-Dplugin_upower=true,-Dplugin_upower=false"

# Always disable these plugins on non-x86 platforms as they don't compile or are useless
DISABLE_NON_X86 = "plugin_amt plugin_intel_spi plugin_msr plugin_thunderbolt"
DISABLE_NON_X86:x86 = ""
DISABLE_NON_X86:x86-64 = ""
PACKAGECONFIG:remove = "${DISABLE_NON_X86}"

FILES:${PN} += "${libdir}/fwupd-plugins-* \
                ${systemd_unitdir} \
                ${datadir}/fish \
                ${datadir}/metainfo \
                ${datadir}/icons \
                ${datadir}/dbus-1 \
                ${datadir}/polkit-1 \
                ${nonarch_libdir}/modules-load.d"

FILES:${PN}-ptest += "${libexecdir}/installed-tests/ \
                      ${datadir}/installed-tests/"
RDEPENDS:${PN}-ptest += "gnome-desktop-testing"
