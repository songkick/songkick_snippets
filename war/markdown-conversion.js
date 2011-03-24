$(
  function() {
    var converter = new Showdown.converter();
    $(".snippet-body").each(function(){
      var node_html = $(this).html();
      var md_html = converter.makeHtml(node_html.replace(/(\n\s*<br>|<br>\s*\n)/g, "\n").replace(/(<br>|\s*<\/?p>\s*)/g, "\n"));
      $(this).html(md_html);
    });
  }
)
