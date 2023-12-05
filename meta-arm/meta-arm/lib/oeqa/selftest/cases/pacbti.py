from oeqa.selftest.case import OESelftestTestCase
from oeqa.core.decorator import OETestTag
from oeqa.core.decorator.data import skipIfNotArch
from oeqa.utils.commands import bitbake

@OETestTag("meta-arm")
class PacBtiTest(OESelftestTestCase):

    @skipIfNotArch(["aarch64"])
    def test_pac_bti(self):
        bitbake("test-pacbti")
