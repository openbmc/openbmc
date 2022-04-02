Release 3.5 (kirkstone)
=======================

This section provides migration information for moving to the Yocto
Project 3.5 Release (codename "kirkstone") from the prior release.

Recipe changes
--------------

- To use more `inclusive language <https://inclusivenaming.org/>`__
  in the code and documentation, some variables have been renamed or even
  deleted. BitBake will stop with an error when renamed or removed variables
  still exist in your recipes or configuration.

  Please note that the change applies also to environmental variables, so
  make sure you use a fresh environment for your build.

  The following variables have changed their names:

  - ``BB_ENV_WHITELIST`` became :term:`BB_ENV_PASSTHROUGH`
  - ``BB_ENV_EXTRAWHITE`` became :term:`BB_ENV_PASSTHROUGH_ADDITIONS`
  - ``BB_HASHBASE_WHITELIST`` became :term:`BB_BASEHASH_IGNORE_VARS`
  - ``BB_HASHCONFIG_WHITELIST`` became :term:`BB_HASHCONFIG_IGNORE_VARS`
  - ``BB_HASHTASK_WHITELIST`` became ``BB_TASKHASH_IGNORE_TASKS``
  - ``BB_SETSCENE_ENFORCE_WHITELIST`` became ``BB_SETSCENE_ENFORCE_IGNORE_TASKS``
  - ``CVE_CHECK_PN_WHITELIST`` became :term:`CVE_CHECK_SKIP_RECIPE`
  - ``CVE_CHECK_WHITELIST`` became :term:`CVE_CHECK_IGNORE`
  - ``ICECC_USER_CLASS_BL`` became :term:`ICECC_CLASS_DISABLE`
  - ``ICECC_SYSTEM_CLASS_BL`` became :term:`ICECC_CLASS_DISABLE`
  - ``ICECC_USER_PACKAGE_WL`` became :term:`ICECC_RECIPE_ENABLE`
  - ``ICECC_USER_PACKAGE_BL`` became :term:`ICECC_RECIPE_DISABLE`
  - ``ICECC_SYSTEM_PACKAGE_BL`` became :term:`ICECC_RECIPE_DISABLE`
  - ``LICENSE_FLAGS_WHITELIST`` became :term:`LICENSE_FLAGS_ACCEPTED`
  - ``MULTI_PROVIDER_WHITELIST`` became :term:`BB_MULTI_PROVIDER_ALLOWED`
  - ``PNBLACKLIST`` became :term:`SKIP_RECIPE`
  - ``SDK_LOCAL_CONF_BLACKLIST`` became :term:`ESDK_LOCALCONF_REMOVE`
  - ``SDK_LOCAL_CONF_WHITELIST`` became :term:`ESDK_LOCALCONF_ALLOW`
  - ``SDK_INHERIT_BLACKLIST`` became :term:`ESDK_CLASS_INHERIT_DISABLE`
  - ``SSTATE_DUPWHITELIST`` became ``SSTATE_ALLOW_OVERLAP_FILES``
  - ``SYSROOT_DIRS_BLACKLIST`` became :term:`SYSROOT_DIRS_IGNORE`
  - ``UNKNOWN_CONFIGURE_WHITELIST`` became :term:`UNKNOWN_CONFIGURE_OPT_IGNORE`

  In addition, ``BB_STAMP_WHITELIST``, ``BB_STAMP_POLICY``, ``INHERIT_BLACKLIST``
  and ``TUNEABI_WHITELIST`` have been removed.

  Many internal variable names have been also renamed accordingly.

  In addition, in the ``cve-check`` output, the CVE issue status ``Whitelisted``
  has been renamed to ``Ignored``.

  A :oe_git:`convert-variable-renames.py
  </openembedded-core/tree/scripts/contrib/convert-variable-renames.py>`
  script is provided to convert your recipes and configuration,
  and also warns you about the use of problematic words. The script performs
  changes and you need to review them before committing. An example warning
  looks like::

     poky/scripts/lib/devtool/upgrade.py needs further work at line 275 since it contains abort

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

- Network access from tasks is now disabled by default on kernels which support
  this feature (on most recent distros such as CentOS 8 and Debian 11 onwards).
  This means that tasks accessing the network need to be marked as such with the ``network``
  flag. For example::

     do_mytask[network] = "1"

  This is allowed by default from ``do_fetch`` but not from any of our other standard
  tasks. Recipes shouldn't be accessing the network outside of ``do_fetch`` as it
  usually undermines fetcher source mirroring, image and licence manifests, software
  auditing and supply chain security.

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
  which use :term:`PKG` renaming such as :ref:`ref-classes-debian`.

- :term:`LICENSE` definitions now have to use `SPDX identifiers <https://spdx.org/licenses/>`__.
  A :oe_git:`convert-spdx-licenses.py </openembedded-core/tree/scripts/contrib/convert-spdx-licenses.py>`
  script can be used to update your recipes.

- :term:`SRC_URI`: a new :ref:`bitbake:bitbake-user-manual/bitbake-user-manual-fetching:crate fetcher (\`\`crate://\`\`)`
  is available for Rust packages.

Class changes
-------------

- The ``distutils*.bbclasses`` have been moved to ``meta-python``. The classes and
  `DISTUTILS*` variables have been removed from the documentation.

- ``blacklist.bbclass`` is removed and the functionality moved to the
  :ref:`base <ref-classes-base>` class with a more descriptive
  ``varflag`` named :term:`SKIP_RECIPE` which will use the `SkipRecipe()`
  function. The usage will remain the same::

     SKIP_RECIPE[my-recipe] = "Reason for skipping recipe"

- The Python package build process based on `wheels <https://pythonwheels.com/>`__.
  Here are the new Python packaging classes that should be used:
  :ref:`python-flit_core <ref-classes-python_flit_core>`,
  :ref:`setuptools_python-build_meta <ref-classes-python_setuptools_build_meta>`
  and :ref:`python_poetry_core <ref-classes-python_poetry_core>`.

- ``image-prelink.bbclass`` class is removed.

- New :ref:`overlayfs <ref-classes-overlayfs>` and
  :ref:`overlayfs-etc <ref-classes-overlayfs-etc>` classes are available
  to make it easier to overlay read-only filesystems (for example)
  with `OverlayFS <https://en.wikipedia.org/wiki/OverlayFS>`__.

Configuration changes
---------------------

- The Yocto Project now allows to reuse Shared State from its autobuilder.
  If the network connection between our server and your machine is faster
  than you would build recipes, you can try to speed up your builds
  by using such Share State and Hash Equivalence by setting::

     BB_SIGNATURE_HANDLER = "OEEquivHash"
     BB_HASHSERVE = "auto"
     BB_HASHSERVE_UPSTREAM = "typhoon.yocto.io:8687"
     SSTATE_MIRRORS ?= "file://.* https://sstate.yoctoproject.org/&YOCTO_DOC_VERSION;/PATH;downloadfilename=PATH"

Supported host distribution changes
-----------------------------------

- New support for `AlmaLinux <https://en.wikipedia.org/wiki/AlmaLinux>`__
  hosts replacing `CentOS <https://en.wikipedia.org/wiki/CentOS>`__.
  The following distribution versions were dropped: CentOS 8, Ubuntu 16.04 and Fedora 30, 31 and 32.

Changes for release notes
-------------------------

- Share State cache: now using `ZStandard (zstd) <https://en.wikipedia.org/wiki/Zstd>`__
  instead of Gzip compression, for better performance.

- BitBake has an improved ``setscene`` task display.

- This release fixes the reproducibility issues with ``rust-llvm`` and ``golang``.
  Recipes in OpenEmbedded-Core are now fully reproducible.

