<?php

use Twig\Environment;
use Twig\Error\LoaderError;
use Twig\Error\RuntimeError;
use Twig\Markup;
use Twig\Sandbox\SecurityError;
use Twig\Sandbox\SecurityNotAllowedTagError;
use Twig\Sandbox\SecurityNotAllowedFilterError;
use Twig\Sandbox\SecurityNotAllowedFunctionError;
use Twig\Source;
use Twig\Template;

/* themes/contrib/wxt_bootstrap/templates/file/file-upload-help.html.twig */
class __TwigTemplate_332c18077928a9815a266a3b1d237e6bbfcfbb3c5e5774af63fffb297f6c8a9c extends \Twig\Template
{
    public function __construct(Environment $env)
    {
        parent::__construct($env);

        $this->parent = false;

        $this->blocks = [
        ];
        $this->sandbox = $this->env->getExtension('\Twig\Extension\SandboxExtension');
        $tags = ["if" => 16];
        $filters = ["safe_join" => 22];
        $functions = [];

        try {
            $this->sandbox->checkSecurity(
                ['if'],
                ['safe_join'],
                []
            );
        } catch (SecurityError $e) {
            $e->setSourceContext($this->getSourceContext());

            if ($e instanceof SecurityNotAllowedTagError && isset($tags[$e->getTagName()])) {
                $e->setTemplateLine($tags[$e->getTagName()]);
            } elseif ($e instanceof SecurityNotAllowedFilterError && isset($filters[$e->getFilterName()])) {
                $e->setTemplateLine($filters[$e->getFilterName()]);
            } elseif ($e instanceof SecurityNotAllowedFunctionError && isset($functions[$e->getFunctionName()])) {
                $e->setTemplateLine($functions[$e->getFunctionName()]);
            }

            throw $e;
        }

    }

    protected function doDisplay(array $context, array $blocks = [])
    {
        // line 16
        if (($context["description"] ?? null)) {
            // line 17
            echo "  ";
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed(($context["description"] ?? null)), "html", null, true);
            echo "<br>
";
        }
        // line 19
        if (($context["popover"] ?? null)) {
            // line 20
            echo "  ";
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed(($context["popover"] ?? null)), "html", null, true);
            echo "
";
        } else {
            // line 22
            echo "  ";
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->renderVar($this->env->getExtension('Drupal\Core\Template\TwigExtension')->safeJoin($this->env, $this->sandbox->ensureToStringAllowed(($context["descriptions"] ?? null)), "<br>"));
            echo "
";
        }
    }

    public function getTemplateName()
    {
        return "themes/contrib/wxt_bootstrap/templates/file/file-upload-help.html.twig";
    }

    public function isTraitable()
    {
        return false;
    }

    public function getDebugInfo()
    {
        return array (  71 => 22,  65 => 20,  63 => 19,  57 => 17,  55 => 16,);
    }

    /** @deprecated since 1.27 (to be removed in 2.0). Use getSourceContext() instead */
    public function getSource()
    {
        @trigger_error('The '.__METHOD__.' method is deprecated since version 1.27 and will be removed in 2.0. Use getSourceContext() instead.', E_USER_DEPRECATED);

        return $this->getSourceContext()->getCode();
    }

    public function getSourceContext()
    {
        return new Source("{#
/**
 * @file
 * Theme override to display help text for file fields.
 *
 * Available variables:
 * - description: The normal description for this field, specified by the user.
 * - descriptions: Lines of help text for uploading a file.
 * - popover: Markup to display the descriptions as a popover instead.
 *
 * @ingroup templates
 *
 * @see template_preprocess_file_upload_help()
 */
#}
{% if description %}
  {{ description }}<br>
{% endif %}
{% if popover %}
  {{ popover }}
{% else %}
  {{ descriptions|safe_join('<br>') }}
{% endif %}
", "themes/contrib/wxt_bootstrap/templates/file/file-upload-help.html.twig", "/var/www/html/tbs-proto1.openplus.ca/themes/contrib/wxt_bootstrap/templates/file/file-upload-help.html.twig");
    }
}
