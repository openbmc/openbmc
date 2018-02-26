HOMEPAGE = "https://github.com/containernetworking/cni"
SUMMARY = "Container Network Interface - networking for Linux containers"
DESCRIPTION = "CNI (Container Network Interface), a Cloud Native Computing \
Foundation project, consists of a specification and libraries for writing \
plugins to configure network interfaces in Linux containers, along with a \
number of supported plugins. CNI concerns itself only with network connectivity \
of containers and removing allocated resources when the container is deleted. \
Because of this focus, CNI has a wide range of support and the specification \
is simple to implement. \
"

SRCREV_cni = "4b9e11a5266fe50222ed00c5973c6ea4a384a4bb"
SRCREV_plugins = "c238c93b5e7c681f1935ff813b30e82f96f6c367"
SRC_URI = "\
	git://github.com/containernetworking/cni.git;nobranch=1;name=cni \
        git://github.com/containernetworking/plugins.git;nobranch=1;destsuffix=plugins;name=plugins \
	"

RPROVIDES_${PN} += "kubernetes-cni"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://src/import/LICENSE;md5=fa818a259cbed7ce8bc2a22d35a464fc"

GO_IMPORT = "import"

PV = "0.6.0+git${SRCREV_cni}"

inherit go
inherit goarch

do_compile() {
	export GOARCH="${TARGET_GOARCH}"
	export GOROOT="${STAGING_LIBDIR_NATIVE}/${TARGET_SYS}/go"
	export GOPATH="${S}/src/import:${S}/src/import/vendor"

	# Pass the needed cflags/ldflags so that cgo
	# can find the needed headers files and libraries
	export CGO_ENABLED="1"
	export CFLAGS=""
	export LDFLAGS=""
	export CGO_CFLAGS="${BUILDSDK_CFLAGS} --sysroot=${STAGING_DIR_TARGET}"
	export CGO_LDFLAGS="${BUILDSDK_LDFLAGS} --sysroot=${STAGING_DIR_TARGET}"

	# link fixups for compilation
	rm -f ${S}/src/import/vendor/src
	ln -sf ./ ${S}/src/import/vendor/src
	rm -rf ${S}/src/import/plugins
	rm -rf ${S}/src/import/vendor/github.com/containernetworking/plugins

	mkdir -p ${S}/src/import/vendor/github.com/containernetworking/cni

	ln -sf ../../../../libcni ${S}/src/import/vendor/github.com/containernetworking/cni/libcni
	ln -sf ../../../../pkg ${S}/src/import/vendor/github.com/containernetworking/cni/pkg
	ln -sf ../../../../cnitool ${S}/src/import/vendor/github.com/containernetworking/cni/cnitool
	ln -sf ${WORKDIR}/plugins ${S}/src/import/vendor/github.com/containernetworking/plugins

	export GOPATH="${S}/src/import/.gopath:${S}/src/import/vendor:${STAGING_DIR_TARGET}/${prefix}/local/go"
	export GOROOT="${STAGING_DIR_NATIVE}/${nonarch_libdir}/${HOST_SYS}/go"

	# Pass the needed cflags/ldflags so that cgo
	# can find the needed headers files and libraries
	export CGO_ENABLED="1"
	export CGO_CFLAGS="${CFLAGS} --sysroot=${STAGING_DIR_TARGET}"
	export CGO_LDFLAGS="${LDFLAGS} --sysroot=${STAGING_DIR_TARGET}"

	cd ${S}/src/import/vendor/github.com/containernetworking/cni/libcni
	go build

	cd ${S}/src/import/vendor/github.com/containernetworking/cni/cnitool
	go build

	cd ${S}/src/import/vendor/github.com/containernetworking/plugins/
	PLUGINS="plugins/meta/* plugins/main/*"
	mkdir -p ${WORKDIR}/plugins/bin/
	for p in $PLUGINS; do
	    plugin="$(basename "$p")"
	    echo "building: $p"
	    go build -o ${WORKDIR}/plugins/bin/$plugin github.com/containernetworking/plugins/$p
	done
}

do_install() {
    localbindir="/opt/cni/bin"

    install -d ${D}${localbindir}
    install -d ${D}/${sysconfdir}/cni/net.d

    install -m 755 ${S}/src/import/cnitool/cnitool ${D}/${localbindir}
    install -m 755 -D ${WORKDIR}/plugins/bin/* ${D}/${localbindir}
}

FILES_${PN} += "/opt/cni/bin/*"

INHIBIT_PACKAGE_STRIP = "1"
INSANE_SKIP_${PN} += "ldflags already-stripped"
