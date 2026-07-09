#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import re

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake, runCmd


class KernelModuleSplit(OESelftestTestCase):
    """
    Tests for cross-recipe kernel module dependency generation.
    """

    def real_module_package(self, recipe):
        """
        Return the name of the real (.ko-containing) package for a kernel
        module recipe, i.e. the one suffixed with ${KERNEL_VERSION}. The version
        is read from the produced package list rather than from KERNEL_VERSION,
        which can be unset at parse time when the module is restored from sstate.
        """
        packages = runCmd("oe-pkgdata-util list-pkgs -p %s" % recipe).output.split()
        version_re = re.compile(r"^%s-(\d[^/]*)$" % re.escape(recipe))
        matches = [p for p in packages if version_re.match(p)]
        self.assertEqual(len(matches), 1,
            "Expected exactly one %s-<version> package, found: %s" % (recipe, matches))
        return matches[0]

    def test_cross_recipe_module_dependency(self):
        """
        Summary:    Regression test for Yocto bug 12290. When one external
                    kernel module recipe consumes an exported symbol from
                    another, the consumer's auto-generated RDEPENDS must point
                    at the exporter's real (-${KERNEL_VERSION}) package, not at
                    the empty unsuffixed kernel-module-* package.
        Expected:   kernel-module-testbar-${KERNEL_VERSION} RDEPENDS
                    kernel-module-testfoo-${KERNEL_VERSION}, does not depend on
                    the bare kernel-module-testfoo package, and RPROVIDES the
                    unsuffixed kernel-module-testbar virtual.
        Product:    OE-Core
        """
        consumer_recipe = "kernel-module-testbar"
        exporter_recipe = "kernel-module-testfoo"

        bitbake("%s %s" % (consumer_recipe, exporter_recipe))

        consumer_pkg = self.real_module_package(consumer_recipe)
        exporter_pkg = self.real_module_package(exporter_recipe)

        rdepends = runCmd("oe-pkgdata-util read-value RDEPENDS %s" % consumer_pkg).output.split()

        # The consumer must depend on the real (.ko-containing) exporter
        # package, i.e. the one suffixed with KERNEL_VERSION ...
        self.assertIn(exporter_pkg, rdepends,
            "%s does not RDEPEND the real exporter package %s (got: %s)"
            % (consumer_pkg, exporter_pkg, rdepends))

        # ... and never on the bare, empty unsuffixed exporter package, which
        # was the failure mode of bug 12290.
        self.assertNotIn(exporter_recipe, rdepends,
            "%s RDEPENDS the empty unsuffixed package %s (bug 12290 regression)"
            % (consumer_pkg, exporter_recipe))

        # The real package must virtual-provide the unsuffixed name so that
        # existing references to kernel-module-testbar still resolve.
        rprovides = runCmd("oe-pkgdata-util read-value RPROVIDES %s" % consumer_pkg).output.split()
        self.assertIn(consumer_recipe, rprovides,
            "%s does not RPROVIDE the virtual %s (got: %s)"
            % (consumer_pkg, consumer_recipe, rprovides))
