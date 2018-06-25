HOMEPAGE = "https://github.com/hyperhq/runv"
SUMMARY = "Hypervisor-based Runtime for OCI"
DESCRIPTION = "Hypervisor-based Runtime for OCI"

SRCREV_runv = "b360a686abc6c6e896382990ef1b93ef07c7a677"
SRC_URI = "\
	git://github.com/hyperhq/runv.git;nobranch=1;name=runv \
	"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://src/import/LICENSE;md5=4106a50540bdec3b9734f9c70882d382"

GO_IMPORT = "import"

PV = "0.4.0+git${SRCREV_runv}"

inherit go
inherit goarch
inherit pkgconfig
inherit autotools-brokensep

PACKAGECONFIG[xen] = "--with-xen,--without-xen,"
AUTOTOOLS_SCRIPT_PATH = "${S}/src/import/"

RDEPENDS_${PN} += " qemu hyperstart"

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

	mkdir -p ${S}/src/import/vendor/github.com/hyperhq/runv

	echo fff
	pwd
	ln -sf src/import/cli
	ln -sf ../../../../api ${S}/src/import/vendor/github.com/hyperhq/runv/api
	ln -sf ../../../../cli ${S}/src/import/vendor/github.com/hyperhq/runv/cli
	ln -sf ../../../../lib ${S}/src/import/vendor/github.com/hyperhq/runv/lib
	ln -sf ../../../../driverloader ${S}/src/import/vendor/github.com/hyperhq/runv/driverloader
	ln -sf ../../../../factory ${S}/src/import/vendor/github.com/hyperhq/runv/factory
	ln -sf ../../../../hyperstart ${S}/src/import/vendor/github.com/hyperhq/runv/hyperstart
	ln -sf ../../../../hypervisor ${S}/src/import/vendor/github.com/hyperhq/runv/hypervisor
	ln -sf ../../../../template ${S}/src/import/vendor/github.com/hyperhq/runv/template

	export GOPATH="${S}/src/import/.gopath:${S}/src/import/vendor:${STAGING_DIR_TARGET}/${prefix}/local/go"
	export GOROOT="${STAGING_DIR_NATIVE}/${nonarch_libdir}/${HOST_SYS}/go"

	# Pass the needed cflags/ldflags so that cgo
	# can find the needed headers files and libraries
	export CGO_ENABLED="1"
	export CGO_CFLAGS="${CFLAGS} --sysroot=${STAGING_DIR_TARGET}"
	export CGO_LDFLAGS="${LDFLAGS} --sysroot=${STAGING_DIR_TARGET}"

	oe_runmake build-runv
}

do_install() {
    localbindir="/usr/local/bin"

    install -d ${D}${localbindir}
    install -m 755 ${S}/runv ${D}/${localbindir}
}

deltask compile_ptest_base

FILES_${PN} += "/usr/local/bin/*"

INHIBIT_PACKAGE_STRIP = "1"
INSANE_SKIP_${PN} += "ldflags already-stripped"
