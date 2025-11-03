SUMMARY = "A simple daemon to allow session software to update firmware"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "glib-2.0 libxmlb json-glib libjcat gcab vala-native python3-jinja2-native"

SRC_URI = "https://github.com/${BPN}/${BPN}/releases/download/${PV}/${BP}.tar.xz \
           file://run-ptest"
SRC_URI[sha256sum] = "4e554f77a8a73383a41d5637b62e6c8a8936e66cd1d18585baf29d7fe47fc4d7"

UPSTREAM_CHECK_URI = "https://github.com/${BPN}/${BPN}/releases"

# Machine-specific as we examine MACHINE_FEATURES to decide whether to build the UEFI plugins
PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit meson vala gobject-introspection systemd bash-completion pkgconfig gi-docgen ptest manpages useradd

GIDOCGEN_MESON_OPTION = 'docs'
GIDOCGEN_MESON_ENABLE_FLAG = 'enabled'
GIDOCGEN_MESON_DISABLE_FLAG = 'disabled'
GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'

PACKAGECONFIG ??= "curl gnutls gudev gusb \
                   ${@bb.utils.filter('DISTRO_FEATURES', 'bluetooth polkit', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd offline', '', d)} \
                   ${@bb.utils.contains('MACHINE_FEATURES', 'efi', 'plugin_uefi_capsule plugin_uefi_pk', '', d)} \
                   ${@bb.utils.contains('PTEST_ENABLED', '1', 'tests', '', d)} \
                   hsi \
                   plugin_android_boot \
                   plugin_acpi_phat \
                   plugin_bcm57xx \
                   plugin_emmc \
                   plugin_ep963x \
                   plugin_fastboot \
                   plugin_flashrom \
                   plugin_gpio \
                   plugin_igsc \
                   plugin_intel_me \
                   plugin_intel_spi \
                   plugin_logitech_bulkcontroller \
                   plugin_logitech_scribe \
                   plugin_modem_manager \
                   plugin_msr \
                   plugin_nitrokey \
                   plugin_nvme \
                   plugin_parade_lspcon \
                   plugin_pixart_rf \
                   plugin_realtek_mst \
                   plugin_redfish \
                   plugin_synaptics_mst \
                   plugin_synaptics_rmi \
                   plugin_scsi \
                   plugin_uf2 \
                   plugin_upower \
                   sqlite"

PACKAGECONFIG[bluetooth] = "-Dbluez=enabled,-Dbluez=disabled"
PACKAGECONFIG[compat-cli] = "-Dcompat_cli=true,-Dcompat_cli=false"
PACKAGECONFIG[consolekit] = "-Dconsolekit=enabled,-Dconsolekit=disabled,consolekit"
PACKAGECONFIG[curl] = "-Dcurl=enabled,-Dcurl=disabled,curl"
PACKAGECONFIG[firmware-packager] = "-Dfirmware-packager=true,-Dfirmware-packager=false"
PACKAGECONFIG[fish-completion] = "-Dfish_completion=true,-Dfish_completion=false"
PACKAGECONFIG[gnutls] = "-Dgnutls=enabled,-Dgnutls=disabled,gnutls"
PACKAGECONFIG[gudev] = "-Dgudev=enabled,-Dgudev=disabled,libgudev"
PACKAGECONFIG[gusb] = "-Dgusb=enabled,-Dgusb=disabled,libgusb"
PACKAGECONFIG[hsi] = "-Dhsi=enabled,-Dhsi=disabled"
PACKAGECONFIG[libarchive] = "-Dlibarchive=enabled,-Dlibarchive=disabled,libarchive"
PACKAGECONFIG[manpages] = "-Dman=true,-Dman=false"
PACKAGECONFIG[metainfo] = "-Dmetainfo=true,-Dmetainfo=false"
PACKAGECONFIG[offline] = "-Doffline=enabled,-Doffline=disabled"
PACKAGECONFIG[polkit] = "-Dpolkit=enabled,-Dpolkit=disabled,polkit"
PACKAGECONFIG[sqlite] = "-Dsqlite=enabled,-Dsqlite=disabled,sqlite3"
PACKAGECONFIG[systemd] = "-Dsystemd=enabled,-Dsystemd=disabled,systemd"
PACKAGECONFIG[tests] = "-Dtests=true,-Dtests=false,gcab-native"


# TODO plugins-all meta-option that expands to all plugin_*?
PACKAGECONFIG[plugin_acpi_phat] = "-Dplugin_acpi_phat=enabled,-Dplugin_acpi_phat=disabled"
PACKAGECONFIG[plugin_android_boot] = "-Dplugin_android_boot=enabled,-Dplugin_android_boot=disabled"
PACKAGECONFIG[plugin_bcm57xx] = "-Dplugin_bcm57xx=enabled,-Dplugin_bcm57xx=disabled"
PACKAGECONFIG[plugin_cfu] = "-Dplugin_cfu=enabled,-Dplugin_cfu=disabled"
PACKAGECONFIG[plugin_emmc] = "-Dplugin_emmc=enabled,-Dplugin_emmc=disabled"
PACKAGECONFIG[plugin_ep963x] = "-Dplugin_ep963x=enabled,-Dplugin_ep963x=disabled"
PACKAGECONFIG[plugin_fastboot] = "-Dplugin_fastboot=enabled,-Dplugin_fastboot=disabled"
PACKAGECONFIG[plugin_flashrom] = "-Dplugin_flashrom=enabled,-Dplugin_flashrom=disabled,flashrom"
PACKAGECONFIG[plugin_gpio] = "-Dplugin_gpio=enabled,-Dplugin_gpio=disabled"
PACKAGECONFIG[plugin_igsc] = "-Dplugin_igsc=enabled,-Dplugin_igsc=disabled"
PACKAGECONFIG[plugin_intel_me] = "-Dplugin_intel_me=enabled,-Dplugin_intel_me=disabled"
PACKAGECONFIG[plugin_intel_spi] = "-Dplugin_intel_spi=true -Dlzma=enabled,-Dplugin_intel_spi=false -Dlzma=disabled,xz"
PACKAGECONFIG[plugin_logitech_bulkcontroller] = "-Dplugin_logitech_bulkcontroller=enabled,-Dplugin_logitech_bulkcontroller=disabled,protobuf-c-native protobuf-c"
PACKAGECONFIG[plugin_logitech_scribe] = "-Dplugin_logitech_scribe=enabled,-Dplugin_logitech_scribe=disabled"
PACKAGECONFIG[plugin_modem_manager] = "-Dplugin_modem_manager=enabled,-Dplugin_modem_manager=disabled,libqmi modemmanager"
PACKAGECONFIG[plugin_msr] = "-Dplugin_msr=enabled,-Dplugin_msr=disabled,cpuid"
PACKAGECONFIG[plugin_nitrokey] = "-Dplugin_nitrokey=enabled,-Dplugin_nitrokey=disabled"
PACKAGECONFIG[plugin_nvme] = "-Dplugin_nvme=enabled,-Dplugin_nvme=disabled"
PACKAGECONFIG[plugin_parade_lspcon] = "-Dplugin_parade_lspcon=enabled,-Dplugin_parade_lspcon=disabled"
PACKAGECONFIG[plugin_pixart_rf] = "-Dplugin_pixart_rf=enabled,-Dplugin_pixart_rf=disabled"
PACKAGECONFIG[plugin_powerd] = "-Dplugin_powerd=enabled,-Dplugin_powerd=disabled"
PACKAGECONFIG[plugin_realtek_mst] = "-Dplugin_realtek_mst=enabled,-Dplugin_realtek_mst=disabled"
PACKAGECONFIG[plugin_redfish] = "-Dplugin_redfish=enabled,-Dplugin_redfish=disabled"
PACKAGECONFIG[plugin_scsi] = "-Dplugin_scsi=enabled,-Dplugin_scsi=disabled"
PACKAGECONFIG[plugin_synaptics_mst] = "-Dplugin_synaptics_mst=enabled,-Dplugin_synaptics_mst=disabled"
PACKAGECONFIG[plugin_synaptics_rmi] = "-Dplugin_synaptics_rmi=enabled,-Dplugin_synaptics_rmi=disabled"
PACKAGECONFIG[plugin_tpm] = "-Dplugin_tpm=enabled,-Dplugin_tpm=disabled,tpm2-tss"
# Turn off the capsule splash as it needs G-I at buildtime, which isn't currently supported
PACKAGECONFIG[plugin_uefi_capsule] = "-Dplugin_uefi_capsule=enabled -Dplugin_uefi_capsule_splash=false,-Dplugin_uefi_capsule=disabled,efivar fwupd-efi"
PACKAGECONFIG[plugin_uefi_pk] = "-Dplugin_uefi_pk=enabled,-Dplugin_uefi_pk=disabled"
PACKAGECONFIG[plugin_uf2] = "-Dplugin_uf2=enabled,-Dplugin_uf2=disabled"
PACKAGECONFIG[plugin_upower] = "-Dplugin_upower=enabled,-Dplugin_upower=disabled"

# Always disable these plugins on non-x86 platforms as they don't compile or are useless
DISABLE_NON_X86 = "plugin_intel_me plugin_intel_spi plugin_msr"
DISABLE_NON_X86:x86 = ""
DISABLE_NON_X86:x86-64 = ""
PACKAGECONFIG:remove = "${DISABLE_NON_X86}"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system --no-create-home --user-group --home-dir ${sysconfdir}/polkit-1 --shell /bin/nologin polkitd"

do_install:append() {
    if ${@bb.utils.contains('PACKAGECONFIG', 'polkit', 'true', 'false', d)}; then
        #Fix up permissions on polkit rules.d to work with rpm4 constraints
        chmod 700 ${D}/${datadir}/polkit-1/rules.d
        chown polkitd:root ${D}/${datadir}/polkit-1/rules.d
    fi
}

FILES:${PN} += "${libdir}/fwupd-plugins-* \
                ${libdir}/fwupd-${PV} \
                ${systemd_unitdir} \
                ${nonarch_libdir}/sysusers.d/fwupd.conf \
                ${datadir}/fish \
                ${datadir}/metainfo \
                ${datadir}/icons \
                ${datadir}/dbus-1 \
                ${datadir}/polkit-1 \
                ${nonarch_libdir}/modules-load.d"

FILES:${PN}-ptest += "${libexecdir}/installed-tests/ \
                      ${datadir}/installed-tests/"
RDEPENDS:${PN}-ptest += "gnome-desktop-testing"

INSANE_SKIP:${PN}-ptest += "buildpaths"
