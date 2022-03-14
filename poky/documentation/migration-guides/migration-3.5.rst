Release 3.5 (kirkstone)
=======================

This section provides migration information for moving to the Yocto
Project 3.5 Release (codename "kirkstone") from the prior release.

Recipe changes
--------------

- Because of the uncertainty in future default branch names in git repositories,
  it is now required to add a branch name to all URLs described
  by ``git://`` and ``gitsm://`` :term:`SRC_URI` entries. For example::

     SRC_URI = "git://git.denx.de/u-boot.git;branch=master"

  A :oe_git:`convert-srcuri </openembedded-core/tree/scripts/contrib/convert-srcuri.py>`
  script to convert your recipes is available in :term:`OpenEmbedded-Core (OE-Core)`
  and in :term:`Poky`.

- Because of `GitHub dropping support for the git:
  protocol <https://github.blog/2021-09-01-improving-git-protocol-security-github/>`__,
  recipes now need to use ``;protocol=https`` at the end of GitHub
  URLs. The same script as above can be used to convert the recipes.

- The :term:`TOPDIR` variable and the current working directory are no longer modified
  when parsing recipes. Any code depending on that behaviour will no longer work.

- The ``append``, ``prepend`` and ``remove`` operators can now only be combined with
  ``=`` and ``:=`` operators. To the exception of the ``append`` plus ``+=`` and
  ``prepend`` plus ``=+`` combinations, all combinations could be factored up to the
  ``append``, ``prepend`` or ``remove`` in the combination. This brought a lot of
  confusion on how the override style syntax operators work and should be used.
  Therefore, those combinations can simply be replaced by a single ``append``,
  ``prepend`` or ``remove`` operator without any additional change.
  For the ``append`` plus ``+=`` (and ``prepend`` plus ``=+``) combinations,
  the content should be prefixed (respectively suffixed) by a space to maintain
  the same behavior.  You can learn more about override style syntax operators
  (``append``, ``prepend`` and ``remove``) in the BitBake documentation:
  :ref:`bitbake:bitbake-user-manual/bitbake-user-manual-metadata:appending and prepending (override style syntax)`
  and :ref:`bitbake:bitbake-user-manual/bitbake-user-manual-metadata:removal (override style syntax)`.

- :ref:`allarch <ref-classes-allarch>` packagegroups can no longer depend on packages
  which use :term:`PKG` renaming such as :ref:`debian.bbclass <ref-classes-debian>`.

