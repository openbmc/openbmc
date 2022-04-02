#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: MIT
#

from oeqa.core.decorator import OETestDecorator, registerDecorator

@registerDecorator
class OEHasPackage(OETestDecorator):
    """
        Checks if image has packages (un)installed.

        The argument must be a string, set, or list of packages that must be
        installed or not present in the image.

        The way to tell a package must not be in an image is using an
        exclamation point ('!') before the name of the package.

        If test depends on pkg1 or pkg2 you need to use:
        @OEHasPackage({'pkg1', 'pkg2'})

        If test depends on pkg1 and pkg2 you need to use:
        @OEHasPackage('pkg1')
        @OEHasPackage('pkg2')

        If test depends on pkg1 but pkg2 must not be present use:
        @OEHasPackage({'pkg1', '!pkg2'})
    """

    attrs = ('need_pkgs',)

    def setUpDecorator(self):
        need_pkgs = set()
        unneed_pkgs = set()

        # Turn literal strings into a list so we can just iterate over it
        if isinstance(self.need_pkgs, str):
            self.need_pkgs = [self.need_pkgs,]

        for pkg in self.need_pkgs:
            if pkg.startswith('!'):
                unneed_pkgs.add(pkg[1:])
            else:
                need_pkgs.add(pkg)

        if unneed_pkgs:
            msg = 'Checking if %s is not installed' % ', '.join(unneed_pkgs)
            self.logger.debug(msg)
            if not self.case.tc.image_packages.isdisjoint(unneed_pkgs):
                msg = "Test can't run with %s installed" % ', or '.join(unneed_pkgs)
                self._decorator_fail(msg)

        if need_pkgs:
            msg = 'Checking if at least one of %s is installed' % ', '.join(need_pkgs)
            self.logger.debug(msg)
            if self.case.tc.image_packages.isdisjoint(need_pkgs):
                msg = "Test requires %s to be installed" % ', or '.join(need_pkgs)
                self._decorator_fail(msg)

    def _decorator_fail(self, msg):
        self.case.skipTest(msg)

@registerDecorator
class OERequirePackage(OEHasPackage):
    """
        Checks if image has packages (un)installed.
        It is almost the same as OEHasPackage, but if dependencies are missing
        the test case fails.

        The argument must be a string, set, or list of packages that must be
        installed or not present in the image.

        The way to tell a package must not be in an image is using an
        exclamation point ('!') before the name of the package.

        If test depends on pkg1 or pkg2 you need to use:
        @OERequirePackage({'pkg1', 'pkg2'})

        If test depends on pkg1 and pkg2 you need to use:
        @OERequirePackage('pkg1')
        @OERequirePackage('pkg2')

        If test depends on pkg1 but pkg2 must not be present use:
        @OERequirePackage({'pkg1', '!pkg2'})
    """

    def _decorator_fail(self, msg):
        self.case.fail(msg)
