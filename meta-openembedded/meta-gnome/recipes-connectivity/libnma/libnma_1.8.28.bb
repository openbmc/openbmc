SUMMARY = "NetworkManager GUI library"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "glib-2.0 gtk+3 networkmanager"

GNOMEBASEBUILDCLASS = "meson"
inherit gnomebase gobject-introspection gtk-doc gettext vala features_check

REQUIRED_DISTRO_FEATURES = "${@bb.utils.contains('PACKAGECONFIG','gcr','x11','',d)}"
ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

SRC_URI[archive.md5sum] = "094c45d7694b153612cbdc3c713edcb5"
SRC_URI[archive.sha256sum] = "4af69552d131a3b2b8b6a2df584044258bf588448dcdb4bddfa12a07c134b726"

PACKAGECONFIG ?= "gcr iso_codes mobile_broadband_provider_info"
PACKAGECONFIG[gcr] = "-Dgcr=true,-Dgcr=false,gcr"
PACKAGECONFIG[iso_codes] = "-Diso_codes=true,-Diso_codes=false,iso-codes,iso-codes"
PACKAGECONFIG[mobile_broadband_provider_info] = "-Dmobile_broadband_provider_info=true,-Dmobile_broadband_provider_info=false,mobile-broadband-provider-info,mobile-broadband-provider-info"

# go introspection is not supported for mipsn32/riscv32, but vapi needs it
#
EXTRA_OEMESON_mipsarchn32_append = " -Dvapi=false"
EXTRA_OEMESON_riscv32_append = " -Dvapi=false"

GTKDOC_MESON_OPTION = "gtk_doc"
