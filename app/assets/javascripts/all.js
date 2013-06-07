require('bootstrap/bootstrap.js');
require('jquery/jquery-fixup.js');

String.prototype.trim = function() {
	return this.replace(/^\s+|\s+$/g, "");
}

require('base/base.js');
