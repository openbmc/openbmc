require ${BPN}.inc

SRC_URI += "file://${BPN}.initd \
            file://0001-Remove-modules_clean-from-clean-target.patch \
            "

SRC_URI:append:libc-musl = " file://Drop-use-of-error-h.patch"

inherit update-rc.d

INITSCRIPT_NAME = "${BPN}"



do_compile() {
    oe_runmake nbcat
    oe_runmake mkemlog
}

do_install() {
   install -Dm 0755 ${WORKDIR}/${BPN}.initd ${D}${sysconfdir}/init.d/${BPN}
   install -Dm 0755 ${S}/nbcat ${D}${bindir}/nbcat
   install -Dm 0755 ${S}/mkemlog ${D}${bindir}/mkemlog
}

RRECOMMENDS:${PN} += "kernel-module-emlog"

CVE_STATUS_GROUPS += "CVE_STATUS_EMLOG"
CVE_STATUS_EMLOG[status] = "fixed-version: The name of this product is exactly the same as github.com/emlog/emlog. CVE can be safely ignored."
CVE_STATUS_EMLOG = " \
    CVE-2019-16868 \
    CVE-2019-17073 \
    CVE-2021-44584 \
    CVE-2022-1526 \
    CVE-2022-3968 \
    CVE-2023-43291 \
"
