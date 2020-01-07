 var issueType = "";
    jQuery(document).ready(
      function(){
        jQuery("input[name=pay-issue]").on( "change", function() {
          issueType = this.id; //issueType becomes the ID of their choice
          if (issueType == 'update')
          {
            jQuery("#deductions-help").hide("slow");
            jQuery("#not-full-pay-help").hide("slow");
            jQuery("#no-pay-help").hide("slow");
            jQuery("#overpayment-help").hide("slow");
            jQuery("#other-help").hide("slow");
            jQuery("#update-help").show("slow");
          }
          else if (issueType=='overpayment')
          {
            jQuery("#deductions-help").hide("slow");
            jQuery("#not-full-pay-help").hide("slow");
            jQuery("#no-pay-help").hide("slow");
            jQuery("#update-help").hide("slow");
            jQuery("#other-help").hide("slow");
            jQuery("#overpayment-help").show("slow");
          }
          else if (issueType=='no-pay')
          {
            jQuery("#deductions-help").hide("slow");
            jQuery("#not-full-pay-help").hide("slow");
            jQuery("#update-help").hide("slow");
            jQuery("#overpayment-help").hide("slow");
            jQuery("#other-help").hide("slow");
            jQuery("#no-pay-help").show("slow");
          }
          else if (issueType=='not-full-pay')
          {
            jQuery("#deductions-help").hide("slow");
            jQuery("#update-help").hide("slow");
            jQuery("#overpayment-help").hide("slow");
            jQuery("#no-pay-help").hide("slow");
            jQuery("#other-help").hide("slow");
            jQuery("#not-full-pay-help").show("slow");
          }
          else if (issueType=='deductions')
          {
            jQuery("#update-help").hide("slow");
            jQuery("#overpayment-help").hide("slow");
            jQuery("#no-pay-help").hide("slow");
            jQuery("#not-full-pay-help").hide("slow");
            jQuery("#other-help").hide("slow");
            jQuery("#deductions-help").show("slow");
          }
          else if (issueType=='other')
          {
            jQuery("#update-help").hide("slow");
            jQuery("#overpayment-help").hide("slow");
            jQuery("#no-pay-help").hide("slow");
            jQuery("#not-full-pay-help").hide("slow");
            jQuery("#deductions-help").hide("slow");
            jQuery("#other-help").show("slow");
          }
        });
      });

    var hireDate= "";
    jQuery(document).ready(
      function(){
        jQuery("input[name=new-hire]").on( "change", function() {
          hireDate = this.id; //issueType becomes the ID of their choice
          if (hireDate == 'yes-recent')
          {
            jQuery("#2not-recent").hide("slow");
            jQuery("#2recent-hire").show("slow");
          }
          else if (hireDate=='no-recent')
          {
            jQuery("#2recent-hire").hide("slow");
            jQuery("#2not-recent").show("slow");
          }
        });
      });(jQuery);