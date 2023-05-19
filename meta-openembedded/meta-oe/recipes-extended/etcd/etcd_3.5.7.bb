DESCRIPTION = "etcd is a distributed key-value store for distributed systems"
HOMEPAGE = "https://etcd.io/"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/${GO_INSTALL}/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI = " \
    git://github.com/etcd-io/etcd;branch=release-3.5;protocol=https \
    file://0001-xxhash-bump-to-v2.1.2.patch;patchdir=src/${GO_IMPORT} \
    file://0001-test_lib.sh-remove-gobin-requirement-during-build.patch;patchdir=src/${GO_IMPORT} \
    file://etcd.service \
    file://etcd-existing.conf \
    file://etcd-new.service \
    file://etcd-new.path \
"

SRCREV = "215b53cf3b48ee761f4c40908b3874b2e5e95e9f"
UPSTREAM_CHECK_COMMITS = "1"

GO_IMPORT = "go.etcd.io/etcd/v3"
GO_INSTALL = "src/${GO_IMPORT}/"

RDEPENDS:${PN}-dev = " \
    bash \
"

export GO111MODULE="on"

inherit go systemd pkgconfig features_check

# Go based binaries do not handle being stripped
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"

# network is required by go to get dependent packages
do_compile[network] = "1"

# Need to build etcd out of where it is extracted to
# Need to directly call build script vs. "make build"
# because "make build" executes the generated binaries
# at the end of the build which do not run correctly
# when cross compiling for another machine
go_do_compile:prepend() {
    cd ${GO_INSTALL}
    ./build.sh


    # Lots of discussion in go community about how it sets packages to
    # read-only by default -> https://github.com/golang/go/issues/31481
    # etcd is going to need some upstream work to support it.
    # For now, set the packages which are read-only back to
    # writeable so things like "bitbake -c cleanall etcd" will work.
    chmod u+w -R ${WORKDIR}/build/pkg/mod
}

REQUIRED_DISTRO_FEATURES = "systemd"
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN}:append = " etcd.service etcd-new.service etcd-new.path"

do_install:append() {
    install -d ${D}${bindir}/
    install -m 0755 ${D}${libdir}/go/src/go.etcd.io/etcd/v3/bin/etcd ${D}${bindir}
    install -m 0755 ${D}${libdir}/go/src/go.etcd.io/etcd/v3/bin/etcdctl ${D}${bindir}
    install -m 0755 ${D}${libdir}/go/src/go.etcd.io/etcd/v3/bin/etcdutl ${D}${bindir}
    install -m 0644 ${WORKDIR}/etcd-existing.conf -D -t ${D}${sysconfdir}/etcd.d
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/etcd.service ${D}${systemd_system_unitdir}/
    install -m 0644 ${WORKDIR}/etcd-new.service ${D}${systemd_system_unitdir}/
    install -m 0644 ${WORKDIR}/etcd-new.path ${D}${systemd_system_unitdir}/
}

FILES:${PN}:append = " ${sysconfdir}/etcd.d/etcd-existing.conf"

