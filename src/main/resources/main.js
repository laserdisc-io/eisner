var dotToSVG = function(dot) {
  return new Viz().renderString(dot)._future;
}

var svgToRoughSVG = function(svgString) {
  var svg = JSON.parse(svgString);
  var i = 0;
  var patterns = [];
  var elements = [];

  var gen = rough.generator({}, { width: svg.viewBox.w, height: svg.viewBox.h });

  svg.elements.forEach(function(e) {

    var options = function() {
      return {
        fill: e.fill == 'none' ? null : e.fill,
        stroke: e.stroke == 'none' ? null : e.stroke
      };
    };

    var pushAll = function(ps) {
      ps.forEach(function(p) {
        if (p.hasOwnProperty("pattern")) {
          var pattern = p.pattern;
          delete p.pattern;
          pattern.id = e.type + "_pattern" + i;
          patterns.push(pattern);
          p.fill = "url(#" + pattern.id + ")";
          i++;
        }
        p.type = "Path";
        elements.push(p);
      });
    };

    if (e.type == 'Ellipse') {
      pushAll(gen.toPaths(gen.ellipse(e.cx, e.cy, e.rx * 1.5, e.ry * 1.6)));
    } else if (e.type == 'Path') {
      pushAll(gen.toPaths(gen.path(e.d, options())));
    } else if (e.type == 'Polygon') {
      pushAll(gen.toPaths(gen.path("M" + e.points + "Z", options())));
    } else if (e.type == 'Text') {
      e.fontFamily = "sans-serif"
      e.fontSize = 10
      elements.push(e);
    }
  });

  svg.patterns = patterns;
  svg.elements = elements;

  return JSON.stringify(svg);
}