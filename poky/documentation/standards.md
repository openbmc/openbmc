# Standards for contributing to Yocto Project documentation

This document attemps to standardize the way the Yocto Project
documentation is created.

It is currently a work in progress.

## Text standards

This section has not been filled yet

## ReStructured Text Syntax standards

This section has not been filled yet

## Adding screenshots

The preferred format for adding screenshots is
[Portable Network Graphics (PNG)](https://en.wikipedia.org/wiki/Portable_Network_Graphics).
Compared to the JPEG format, PNG is lossless and compresses screenshots better.

Screenshots are stored in a `figures/` subdirectory.

To include a screenshot in PNG format:

    .. image:: figures/user-configuration.png
       :align: center

Depending on the size of the image, you may also shrink it
to prevent it from filling the whole page width:

       :scale: 50%

For some types of screenshots, for example showing
programs displaying photographs or playing video, the JPEG
format may be more appropriate, because it is better at
compressing real-life images:

    .. image:: figures/vlc.jpg
       :align: center
       :scale: 75%

## Adding new diagrams

New diagrams should be added in
[Scalable Vector Graphics (SVG) format](https://en.wikipedia.org/wiki/Scalable_Vector_Graphics).
Thanks to this, diagrams render nicely at any zoom level on generated documentation,
instead of being pixelated.

The recommended tool for creating SVG diagrams for the Yocto Project
documentation is [Inkscape](https://inkscape.org/).

### Colors

The "GNOME HIG Colors" palette is used in our diagrams
(get it from <https://gitlab.gnome.org/Teams/Design/HIG-app-icons/-/blob/master/GNOME%20HIG.gpl>
if you don't get it from Inkscape).

It's easier to use than the default one in Inkscape,
as it has fewer but sufficient colors. This is a way
to guarantee that we use consistent colors across the
diagrams used in our documentation.

See <https://inkscape-manuals.readthedocs.io/en/latest/palette.html>
for details about working with color palettes in Inkscape.

### Template diagram

The `template/` directory contains a `template.svg` file
to make it easier to create diagrams.
In particular, you can use it to copy standard text, shapes,
arrows and clipart to the new SVG document.

### Fonts

The recommended font for description text and labels is `Nimbus Roman`, size 10.
Labels are in bold.

`template.svg` contains ready-to-copy labels.

### Text boxes

Text boxes are rectangle boxes, with rounded corners, and contain centered text
or labels. Their stroke color is slightly darker than their fill color.

See `template.svg` for example boxes with different colors, which are ready
to be reused.

### Clipart

Only [Public Domain](https://en.wikipedia.org/wiki/Public_domain)
files are accepted for clipart. [Openclipart](https://openclipart.org)
is the best known and recommended source of such clipart.

It is also required to state the source of each new clipart in the commit log,
to make it possible to check its origin.

For the sake of consistency across diagrams, we recommend
to use existing cliparts, whenever possible.

If cliparts in `template.svg` do not satisfy your requirements, you can
add to said file new cliparts, from e.g. Openclipart. Newly added
cliparts shall stay consistent in style and color with existing ones.
