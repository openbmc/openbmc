#
# This class is used by yocto-check-layer script for additional per-recipe tests
# The first test ensures that the layer has no recipes skipping 'installed-vs-shipped' QA checks
#

WARN_QA:remove = "installed-vs-shipped"
ERROR_QA:append = " installed-vs-shipped"

python () {
    packages = set((d.getVar('PACKAGES') or '').split())
    for package in packages:
        skip = set((d.getVar('INSANE_SKIP') or "").split() +
                   (d.getVar('INSANE_SKIP:' + package) or "").split())
        if 'installed-vs-shipped' in skip:
            oe.qa.handle_error("installed-vs-shipped", 'Package %s is skipping "installed-vs-shipped" QA test.' % package, d)
}
