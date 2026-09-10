.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

**********************
Build Process Security
**********************

The :term:`OpenEmbedded Build System` is used to run the builds and careful
consideration has gone into how it does this with the aim of being both secure
and reproducible. Like any system, it does need to be used carefully and in
keeping with the design for that to be true. Users of the system should
consider that:

-  The builds generally aim for any input into the build process being verified in
   some form. For source code tarballs, these would have a checksum. Git source
   trees would have a specific git revision. Metadata would also usually be
   under source control and also have revisions.

   See the
   :doc:`bitbake:bitbake-user-manual/bitbake-user-manual-fetching` section
   of the BitBake User Manual for more information.

-  Some elements that can influence the build are not verified. It is assumed
   that the operating system running the system is secure and of a known setup and
   version. The system goes to significant lengths to isolate against host
   contamination of the output but it is certainly possible, especially maliciously.

   See the :ref:`system-requirements-supported-distros` section of the Yocto
   Project Reference Manual for more information on supported host distributions.

-  The builds assume :term:`DL_DIR` is a safe location. Once download artefacts enter
   that location they are not repeatedly re-verified. A user could edit the git trees or
   tarballs there in ways the build might not detect.

-  The builds assume sstate objects from :term:`SSTATE_DIR` or from a configured
   :doc:`sstate mirror </dev-manual/sstate-mirrors-setup>` are safe (with
   :doc:`signature checks </security-manual/sstate-signing>` if configured).

-  The core build tool, :term:`BitBake`, is an execution engine and will execute code both
   during builds and when parsing recipes. This is not a security issue, it is an
   essential part of its function and purpose.

-  :term:`OpenEmbedded-Core (OE-Core)` is well tested for reproducibility issues but other
   layers and their recipes and code may not be as well tested. Those reproducibility tests
   are available for others to run against their own layers and code.

-  The builds combine many different software components and we take it on trust
   that there aren't issues in those code bases. We'd recommend build environments
   being set up in such a way that if such an issue were ever discovered, which at
   some point could happen, the build environments themselves could be simply
   destroyed and rebuilt cleanly, i.e. they're disposable.
