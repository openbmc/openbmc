

PACKAGES += "\
   packagegroup-security-hardening \
   "
RDEPENDS:packagegroup-core-security += "\
   packagegroup-security-hardening \
   "

SUMMARY:packagegroup-security-hardening = "Security Hardening tools"
RDEPENDS:packagegroup-security-hardening = " \
    bastille \
    "

RDEPENDS:packagegroup-security-scanners += "\
     nikto \
     checksecurity \
     "
