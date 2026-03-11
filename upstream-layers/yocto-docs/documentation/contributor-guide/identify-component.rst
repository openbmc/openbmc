.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Identify the component
**********************

The Yocto Project and OpenEmbedded ecosystem is built of :term:`layers <Layer>`
so the first step is to identify the component where the issue likely lies.
For example, if you have a hardware issue, it is likely related to the BSP
you are using and the best place to seek advice would be from the BSP provider
or :term:`layer`. If the issue is a build/configuration one and a distro is in
use, they would likely be the first place to ask questions. If the issue is a
generic one and/or in the core classes or metadata, the core layer or BitBake
might be the appropriate component.

Each metadata layer being used should contain a ``README`` file and that should
explain where to report issues, where to send changes and how to contact the
maintainers.

If the issue is in the core metadata layer (OpenEmbedded-Core) or in BitBake,
issues can be reported in the :yocto_bugs:`Yocto Project Bugzilla <>`. The
:yocto_lists:`yocto </g/yocto>` mailing list is a general “catch-all” location
where questions can be sent if you can’t work out where something should go.

:term:`Poky` is a commonly used “combination” repository where multiple
components have been combined (:oe_git:`bitbake </bitbake>`,
:oe_git:`openembedded-core </openembedded-core>`,
:yocto_git:`meta-yocto </meta-yocto>` and
:yocto_git:`yocto-docs </yocto-docs>`). Patches should be submitted against the
appropriate individual component rather than :term:`Poky` itself as detailed in
the appropriate ``README`` file.

