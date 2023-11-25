SUMMARY = "UPnP framework"
DESCRIPTION = "GUPnP is an elegant, object-oriented open source framework for creating UPnP  devices and control points, written in C using GObject and libsoup. The GUPnP API is intended to be easy to use, efficient and flexible. It provides the same set of features as libupnp, but shields the developer from most of UPnP's internals."
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "e2fsprogs gssdp libsoup-3.0 libxml2"

inherit gnomebase pkgconfig vala gobject-introspection

SRC_URI[archive.sha256sum] = "c9dc50e8c78b3792d1b0e6c5c5f52c93e9345d3dae2891e311a993a574f5a04f"

SYSROOT_PREPROCESS_FUNCS += "gupnp_sysroot_preprocess"

gupnp_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${bindir_crossscripts}/
    install -m 755 ${D}${bindir}/gupnp-binding-tool* ${SYSROOT_DESTDIR}${bindir_crossscripts}/
}

FILES:${PN}-dev += "${bindir}/gupnp-binding-tool*"

RDEPENDS:${PN}-dev += "python3-core python3-xml"
