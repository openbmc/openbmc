inherit goarch

GO_PARALLEL_BUILD ?= "${@oe.utils.parallel_make_argument(d, '-p %d')}"

GOROOT_class-native = "${STAGING_LIBDIR_NATIVE}/go"
GOROOT_class-nativesdk = "${STAGING_DIR_TARGET}${libdir}/go"
GOROOT = "${STAGING_LIBDIR}/go"
export GOROOT
export GOROOT_FINAL = "${libdir}/go"
export GOCACHE = "${B}/.cache"

export GOARCH = "${TARGET_GOARCH}"
export GOOS = "${TARGET_GOOS}"
export GOHOSTARCH="${BUILD_GOARCH}"
export GOHOSTOS="${BUILD_GOOS}"

GOARM[export] = "0"
GOARM_arm_class-target = "${TARGET_GOARM}"
GOARM_arm_class-target[export] = "1"

GO386[export] = "0"
GO386_x86_class-target = "${TARGET_GO386}"
GO386_x86_class-target[export] = "1"

GOMIPS[export] = "0"
GOMIPS_mips_class-target = "${TARGET_GOMIPS}"
GOMIPS_mips_class-target[export] = "1"

DEPENDS_GOLANG_class-target = "virtual/${TUNE_PKGARCH}-go virtual/${TARGET_PREFIX}go-runtime"
DEPENDS_GOLANG_class-native = "go-native"
DEPENDS_GOLANG_class-nativesdk = "virtual/${TARGET_PREFIX}go-crosssdk virtual/${TARGET_PREFIX}go-runtime"

DEPENDS_append = " ${DEPENDS_GOLANG}"

GO_LINKSHARED ?= "${@'-linkshared' if d.getVar('GO_DYNLINK') else ''}"
GO_RPATH_LINK = "${@'-Wl,-rpath-link=${STAGING_DIR_TARGET}${libdir}/go/pkg/${TARGET_GOTUPLE}_dynlink' if d.getVar('GO_DYNLINK') else ''}"
GO_RPATH = "${@'-r ${libdir}/go/pkg/${TARGET_GOTUPLE}_dynlink' if d.getVar('GO_DYNLINK') else ''}"
GO_RPATH_class-native = "${@'-r ${STAGING_LIBDIR_NATIVE}/go/pkg/${TARGET_GOTUPLE}_dynlink' if d.getVar('GO_DYNLINK') else ''}"
GO_RPATH_LINK_class-native = "${@'-Wl,-rpath-link=${STAGING_LIBDIR_NATIVE}/go/pkg/${TARGET_GOTUPLE}_dynlink' if d.getVar('GO_DYNLINK') else ''}"
GO_EXTLDFLAGS ?= "${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS} ${GO_RPATH_LINK} ${LDFLAGS}"
GO_LINKMODE ?= ""
GO_LINKMODE_class-nativesdk = "--linkmode=external"
GO_LINKMODE_class-native = "--linkmode=external"
GO_LDFLAGS ?= '-ldflags="${GO_RPATH} ${GO_LINKMODE} -extldflags '${GO_EXTLDFLAGS}'"'
export GOBUILDFLAGS ?= "-v ${GO_LDFLAGS} -trimpath"
export GOPATH_OMIT_IN_ACTIONID ?= "1"
export GOPTESTBUILDFLAGS ?= "${GOBUILDFLAGS} -c"
export GOPTESTFLAGS ?= ""
GOBUILDFLAGS_prepend_task-compile = "${GO_PARALLEL_BUILD} "

export GO = "${HOST_PREFIX}go"
GOTOOLDIR = "${STAGING_LIBDIR_NATIVE}/${TARGET_SYS}/go/pkg/tool/${BUILD_GOTUPLE}"
GOTOOLDIR_class-native = "${STAGING_LIBDIR_NATIVE}/go/pkg/tool/${BUILD_GOTUPLE}"
export GOTOOLDIR

export CGO_ENABLED ?= "1"
export CGO_CFLAGS ?= "${CFLAGS}"
export CGO_CPPFLAGS ?= "${CPPFLAGS}"
export CGO_CXXFLAGS ?= "${CXXFLAGS}"
export CGO_LDFLAGS ?= "${LDFLAGS}"

GO_INSTALL ?= "${GO_IMPORT}/..."
GO_INSTALL_FILTEROUT ?= "${GO_IMPORT}/vendor/"

B = "${WORKDIR}/build"
export GOPATH = "${B}"
export GOTMPDIR ?= "${WORKDIR}/go-tmp"
GOTMPDIR[vardepvalue] = ""

python go_do_unpack() {
    src_uri = (d.getVar('SRC_URI') or "").split()
    if len(src_uri) == 0:
        return

    fetcher = bb.fetch2.Fetch(src_uri, d)
    for url in fetcher.urls:
        if fetcher.ud[url].type == 'git':
            if fetcher.ud[url].parm.get('destsuffix') is None:
                s_dirname = os.path.basename(d.getVar('S'))
                fetcher.ud[url].parm['destsuffix'] = os.path.join(s_dirname, 'src', d.getVar('GO_IMPORT')) + '/'
    fetcher.unpack(d.getVar('WORKDIR'))
}

go_list_packages() {
	${GO} list -f '{{.ImportPath}}' ${GOBUILDFLAGS} ${GO_INSTALL} | \
		egrep -v '${GO_INSTALL_FILTEROUT}'
}

go_list_package_tests() {
	${GO} list -f '{{.ImportPath}} {{.TestGoFiles}}' ${GOBUILDFLAGS} ${GO_INSTALL} | \
		grep -v '\[\]$' | \
		egrep -v '${GO_INSTALL_FILTEROUT}' | \
		awk '{ print $1 }'
}

go_do_configure() {
	ln -snf ${S}/src ${B}/
}
do_configure[dirs] =+ "${GOTMPDIR}"

go_do_compile() {
	export TMPDIR="${GOTMPDIR}"
	if [ -n "${GO_INSTALL}" ]; then
		if [ -n "${GO_LINKSHARED}" ]; then
			${GO} install ${GOBUILDFLAGS} `go_list_packages`
			rm -rf ${B}/bin
		fi
		${GO} install ${GO_LINKSHARED} ${GOBUILDFLAGS} `go_list_packages`
	fi
}
do_compile[dirs] =+ "${GOTMPDIR}"
do_compile[cleandirs] = "${B}/bin ${B}/pkg"

go_do_install() {
	install -d ${D}${libdir}/go/src/${GO_IMPORT}
	tar -C ${S}/src/${GO_IMPORT} -cf - --exclude-vcs --exclude '*.test' --exclude 'testdata' . | \
		tar -C ${D}${libdir}/go/src/${GO_IMPORT} --no-same-owner -xf -
	tar -C ${B} -cf - --exclude-vcs --exclude '*.test' --exclude 'testdata' pkg | \
		tar -C ${D}${libdir}/go --no-same-owner -xf -

	if [ -n "`ls ${B}/${GO_BUILD_BINDIR}/`" ]; then
		install -d ${D}${bindir}
		install -m 0755 ${B}/${GO_BUILD_BINDIR}/* ${D}${bindir}/
	fi
}

go_stage_testdata() {
	oldwd="$PWD"
	cd ${S}/src
	find ${GO_IMPORT} -depth -type d -name testdata | while read d; do
		if echo "$d" | grep -q '/vendor/'; then
			continue
		fi
		parent=`dirname $d`
		install -d ${D}${PTEST_PATH}/$parent
		cp --preserve=mode,timestamps -R $d ${D}${PTEST_PATH}/$parent/
	done
	cd "$oldwd"
}

EXPORT_FUNCTIONS do_unpack do_configure do_compile do_install

FILES_${PN}-dev = "${libdir}/go/src"
FILES_${PN}-staticdev = "${libdir}/go/pkg"

INSANE_SKIP_${PN} += "ldflags"

# Add -buildmode=pie to GOBUILDFLAGS to satisfy "textrel" QA checking, but mips
# doesn't support -buildmode=pie, so skip the QA checking for mips/rv32 and its
# variants.
python() {
    if 'mips' in d.getVar('TARGET_ARCH') or 'riscv32' in d.getVar('TARGET_ARCH'):
        d.appendVar('INSANE_SKIP_%s' % d.getVar('PN'), " textrel")
    else:
        d.appendVar('GOBUILDFLAGS', ' -buildmode=pie')
}
