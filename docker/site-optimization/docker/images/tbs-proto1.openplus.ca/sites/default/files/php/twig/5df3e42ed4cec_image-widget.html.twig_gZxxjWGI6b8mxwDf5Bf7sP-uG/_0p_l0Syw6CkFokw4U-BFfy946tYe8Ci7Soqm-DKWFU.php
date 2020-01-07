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

/* themes/contrib/wxt_bootstrap/templates/file/image-widget.html.twig */
class __TwigTemplate_033ccd2c5510c267b7f7c589da801e75ede84258c38382c4ff01b266a24a354c extends \Twig\Template
{
    public function __construct(Environment $env)
    {
        parent::__construct($env);

        $this->parent = false;

        $this->blocks = [
        ];
        $this->sandbox = $this->env->getExtension('\Twig\Extension\SandboxExtension');
        $tags = ["if" => 15];
        $filters = ["without" => 22];
        $functions = [];

        try {
            $this->sandbox->checkSecurity(
                ['if'],
                ['without'],
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
        // line 15
        if ($this->getAttribute(($context["data"] ?? null), "preview", [])) {
            // line 16
            echo "  <div";
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["attributes"] ?? null), "addClass", [0 => "row"], "method")), "html", null, true);
            echo ">
    <div class=\"preview col-sm-2\">
      ";
            // line 18
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["data"] ?? null), "preview", [])), "html", null, true);
            echo "
    </div>
    <div class=\"data col-sm-10\">
      ";
            // line 22
            echo "      ";
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, twig_without($this->sandbox->ensureToStringAllowed(($context["data"] ?? null)), "preview"), "html", null, true);
            echo "
    </div>
  </div>
";
        } else {
            // line 26
            echo "  <div";
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["attributes"] ?? null), "addClass", [0 => ($context["classes"] ?? null)], "method")), "html", null, true);
            echo ">
    ";
            // line 27
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed(($context["data"] ?? null)), "html", null, true);
            echo "
  </div>
";
        }
    }

    public function getTemplateName()
    {
        return "themes/contrib/wxt_bootstrap/templates/file/image-widget.html.twig";
    }

    public function isTraitable()
    {
        return false;
    }

    public function getDebugInfo()
    {
        return array (  82 => 27,  77 => 26,  69 => 22,  63 => 18,  57 => 16,  55 => 15,);
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
 * Theme override for an image field widget.
 *
 * Available variables:
 * - attributes: HTML attributes for the containing element.
 * - data: Render elements of the image widget.
 *
 * @ingroup templates
 *
 * @see template_preprocess_image_widget()
 */
#}
{% if data.preview %}
  <div{{ attributes.addClass('row') }}>
    <div class=\"preview col-sm-2\">
      {{ data.preview }}
    </div>
    <div class=\"data col-sm-10\">
      {# Render widget data without the image preview that was output already. #}
      {{ data|without('preview') }}
    </div>
  </div>
{% else %}
  <div{{ attributes.addClass(classes) }}>
    {{ data }}
  </div>
{% endif %}
", "themes/contrib/wxt_bootstrap/templates/file/image-widget.html.twig", "/var/www/html/tbs-proto1.openplus.ca/themes/contrib/wxt_bootstrap/templates/file/image-widget.html.twig");
    }
}
