from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake

class GitCheck(OESelftestTestCase):
    def test_git_intercept(self):
        """
        Git binaries with CVE-2022-24765 fixed will refuse to operate on a
        repository which is owned by a different user. This breaks our
        do_install task as that runs inside pseudo, so the git repository is
        owned by the build user but git is running as (fake)root.

        We have an intercept which disables pseudo, so verify that it works.
        """
        bitbake("git-submodule-test -c test_git_as_user")
        bitbake("git-submodule-test -c test_git_as_root")
