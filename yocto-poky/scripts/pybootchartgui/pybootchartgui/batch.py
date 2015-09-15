#  This file is part of pybootchartgui.

#  pybootchartgui is free software: you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation, either version 3 of the License, or
#  (at your option) any later version.

#  pybootchartgui is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.

#  You should have received a copy of the GNU General Public License
#  along with pybootchartgui. If not, see <http://www.gnu.org/licenses/>.

import cairo
from . import draw
from .draw import RenderOptions

def render(writer, trace, app_options, filename):
    handlers = {
        "png": (lambda w, h: cairo.ImageSurface(cairo.FORMAT_ARGB32, w, h), \
                lambda sfc: sfc.write_to_png(filename)),
        "pdf": (lambda w, h: cairo.PDFSurface(filename, w, h), lambda sfc: 0),
        "svg": (lambda w, h: cairo.SVGSurface(filename, w, h), lambda sfc: 0)
    }

    if app_options.format is None:
        fmt = filename.rsplit('.', 1)[1]
    else:
        fmt = app_options.format

    if not (fmt in handlers):
        writer.error ("Unknown format '%s'." % fmt)
        return 10

    make_surface, write_surface = handlers[fmt]
    options = RenderOptions (app_options)
    (w, h) = draw.extents (options, 1.0, trace)
    w = max (w, draw.MIN_IMG_W)
    surface = make_surface (w, h)
    ctx = cairo.Context (surface)
    draw.render (ctx, options, 1.0, trace)
    write_surface (surface)
    writer.status ("bootchart written to '%s'" % filename)

