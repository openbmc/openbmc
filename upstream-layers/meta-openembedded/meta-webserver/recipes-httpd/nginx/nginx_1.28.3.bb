require nginx.inc

SRC_URI:append = " \
    file://CVE-2026-40701.patch \
    file://CVE-2026-42934.patch \
    file://CVE-2026-42946_p1.patch \
    file://CVE-2026-42946_p2.patch \
    file://CVE-2026-42945.patch \
    file://CVE-2026-9256.patch \
    file://CVE-2026-42055.patch \
    file://CVE-2026-48142.patch \
    file://CVE-2026-42533_p1.patch \
    file://CVE-2026-42533_p2.patch \
    file://CVE-2026-42533_p3.patch \
    file://CVE-2026-42533_p4.patch \
    file://CVE-2026-42533_p5.patch \
    file://CVE-2026-56434.patch \
"

LIC_FILES_CHKSUM = "file://LICENSE;md5=3dc49537b08b14c8b66ad247bb4c4593"

SRC_URI[sha256sum] = "2c96a946bfb0882a21744ed429770a2123ae1828c7c48665092993ddee91a918"

CVE_STATUS[CVE-2025-53859] = "cpe-stable-backport: Fix is included in 1.28.1"
CVE_STATUS[CVE-2026-1642] = "fixed-version: fixed since v1.28.2"
CVE_STATUS[CVE-2026-40460] = "${@ 'unpatched' if '--with-http_v3_module' in ((d.getVar('EXTRA_OECONF') or '').split() + (d.getVar('PACKAGECONFIG_CONFARGS') or '').split()) else 'not-applicable-config: ngx_http_v3_module is not enabled'}"
CVE_STATUS[CVE-2026-60005] = "${@ 'unpatched' if '--with-http_slice_module' in ( (d.getVar('EXTRA_OECONF') or '').split() + (d.getVar('PACKAGECONFIG_CONFARGS') or '').split()) else 'not-applicable-config: ngx_http_slice_module is not enabled' }"
