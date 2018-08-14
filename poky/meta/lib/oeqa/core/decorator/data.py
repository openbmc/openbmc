# Copyright (C) 2016 Intel Corporation
# Released under the MIT license (see COPYING.MIT)

from oeqa.core.exception import OEQAMissingVariable

from . import OETestDecorator, registerDecorator

def has_feature(td, feature):
    """
        Checks for feature in DISTRO_FEATURES or IMAGE_FEATURES.
    """

    if (feature in td.get('DISTRO_FEATURES', '') or
        feature in td.get('IMAGE_FEATURES', '')):
        return True
    return False

@registerDecorator
class skipIfDataVar(OETestDecorator):
    """
        Skip test based on value of a data store's variable.

        It will get the info of var from the data store and will
        check it against value; if are equal it will skip the test
        with msg as the reason.
    """

    attrs = ('var', 'value', 'msg')

    def setUpDecorator(self):
        msg = ('Checking if %r value is %r to skip test' %
               (self.var, self.value))
        self.logger.debug(msg)
        if self.case.td.get(self.var) == self.value:
            self.case.skipTest(self.msg)

@registerDecorator
class skipIfNotDataVar(OETestDecorator):
    """
        Skip test based on value of a data store's variable.

        It will get the info of var from the data store and will
        check it against value; if are not equal it will skip the
        test with msg as the reason.
    """

    attrs = ('var', 'value', 'msg')

    def setUpDecorator(self):
        msg = ('Checking if %r value is not %r to skip test' %
               (self.var, self.value))
        self.logger.debug(msg)
        if not self.case.td.get(self.var) == self.value:
            self.case.skipTest(self.msg)

@registerDecorator
class skipIfNotInDataVar(OETestDecorator):
    """
        Skip test if value is not in data store's variable.
    """

    attrs = ('var', 'value', 'msg')
    def setUpDecorator(self):
        msg = ('Checking if %r value is in %r to run '
              'the test' % (self.var, self.value))
        self.logger.debug(msg)
        if not self.value in self.case.td.get(self.var):
            self.case.skipTest(self.msg)

@registerDecorator
class OETestDataDepends(OETestDecorator):
    attrs = ('td_depends',)

    def setUpDecorator(self):
        for v in self.td_depends:
            try:
                value = self.case.td[v]
            except KeyError:
                raise OEQAMissingVariable("Test case need %s variable but"\
                        " isn't into td" % v)

@registerDecorator
class skipIfNotFeature(OETestDecorator):
    """
        Skip test based on DISTRO_FEATURES.

        value must be in distro features or it will skip the test
        with msg as the reason.
    """

    attrs = ('value', 'msg')

    def setUpDecorator(self):
        msg = ('Checking if %s is in DISTRO_FEATURES '
               'or IMAGE_FEATURES' % (self.value))
        self.logger.debug(msg)
        if not has_feature(self.case.td, self.value):
            self.case.skipTest(self.msg)
