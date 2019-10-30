var viz = function(dot) {
  return new Viz().renderString(dot)._future;
}

var extractOptions = function (el) {
  return {
    fill: el.fill == 'none' ? null : el.fill,
    stroke: el.stroke == 'none' ? null : el.stroke
  };
}

var r = function(g0) {
  var g = JSON.parse(g0);
  var ps = [];
  var i = 0;
  var es = [];
  var gen = rough.generator({}, { width: g.viewBox.w, height: g.viewBox.h });
  g.elements.forEach(function(el) {
    if (el.type == 'Ellipse') {
      gen.toPaths(gen.ellipse(el.cx, el.cy, el.rx * 1.5, el.ry * 1.6)).forEach(function (path) {
        if (path.hasOwnProperty("pattern")) {
          var pattern = path.pattern;
          delete path.pattern;
          pattern.id = "ellipse_pat" + i;
          ps.push(pattern);
          path.fill = "url(#" + pattern.id + ")";
          i++;
        }
        path.type = "Path";
        es.push(path);
      });
    } else if (el.type == 'Path') {
      gen.toPaths(gen.path(el.d, extractOptions(el))).forEach(function (path) {
        if (path.hasOwnProperty("pattern")) {
          var pattern = path.pattern;
          delete path.pattern;
          pattern.id = "path_pat" + i;
          ps.push(pattern);
          path.fill = "url(#" + pattern.id + ")";
          i++;
        }
        path.type = "Path";
        es.push(path);
      });
    } else if (el.type == 'Polygon') {
      gen.toPaths(gen.path("M" + el.points + "Z", extractOptions(el))).forEach(function (path) {
        if (path.hasOwnProperty("pattern")) {
          var pattern = path.pattern;
          delete path.pattern;
          pattern.id = "polygon_pat" + i;
          ps.push(pattern);
          path.fill = "url(#" + pattern.id + ")";
          i++;
        }
        path.type = "Path";
        es.push(path);
      });
    } else if (el.type == 'Text') {
      el.fontFamily = "sans-serif"
      el.fontSize = 10
      es.push(el);
    }
  });
  g.patterns = ps;
  g.elements = es;
  return JSON.stringify(g);
}