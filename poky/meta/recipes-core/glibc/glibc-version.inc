SRCBRANCH ?= "release/2.38/master"
PV = "2.38+git"
SRCREV_glibc ?= "d37c2b20a4787463d192b32041c3406c2bd91de0"
SRCREV_localedef ?= "e0eca29583b9e0f62645c4316ced93cf4e4e26e1"

GLIBC_GIT_URI ?= "git://sourceware.org/git/glibc.git;protocol=https"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+\.\d+(\.(?!90)\d+)*)"

CVE_STATUS[CVE-2023-4527] = "fixed-version: Fixed in stable branch updates"
CVE_STATUS[CVE-2023-4911] = "fixed-version: Fixed in stable branch updates"
CVE_STATUS[CVE-2023-4806] = "fixed-version: Fixed in stable branch updates"
CVE_STATUS[CVE-2023-5156] = "fixed-version: Fixed in stable branch updates"
CVE_STATUS[CVE-2023-0687] = "fixed-version: Fixed in stable branch updates"
CVE_STATUS[CVE-2023-6246] = "fixed-version: Fixed in stable branch updates"
CVE_STATUS[CVE-2023-6779] = "fixed-version: Fixed in stable branch updates"
CVE_STATUS[CVE-2023-6780] = "fixed-version: Fixed in stable branch updates"
