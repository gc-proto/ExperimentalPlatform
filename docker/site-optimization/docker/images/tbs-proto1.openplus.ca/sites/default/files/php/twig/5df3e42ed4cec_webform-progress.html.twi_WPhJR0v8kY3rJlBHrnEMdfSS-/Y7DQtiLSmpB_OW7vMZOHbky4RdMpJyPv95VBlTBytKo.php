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

/* modules/contrib/webform/templates/webform-progress.html.twig */
class __TwigTemplate_ee6991cd6bd64d0051252cde2b99b861ceb0fad7a9a6c7613fb0394eb74abd5d extends \Twig\Template
{
    public function __construct(Environment $env)
    {
        parent::__construct($env);

        $this->parent = false;

        $this->blocks = [
        ];
        $this->sandbox = $this->env->getExtension('\Twig\Extension\SandboxExtension');
        $tags = ["if" => 26];
        $filters = [];
        $functions = ["attach_library" => 20];

        try {
            $this->sandbox->checkSecurity(
                ['if'],
                [],
                ['attach_library']
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
        // line 20
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->env->getExtension('Drupal\Core\Template\TwigExtension')->attachLibrary("webform/webform.progress"), "html", null, true);
        echo "

<div class=\"webform-progress\">

  ";
        // line 24
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed(($context["bar"] ?? null)), "html", null, true);
        echo "

  ";
        // line 26
        if ((($context["summary"] ?? null) || ($context["percentage"] ?? null))) {
            // line 27
            echo "    <div class=\"webform-progress__status\">
      ";
            // line 28
            if (($context["summary"] ?? null)) {
                // line 29
                echo "        <span class=\"webform-progress__summary\">";
                echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed(($context["summary"] ?? null)), "html", null, true);
                echo "</span>
        ";
                // line 30
                if (($context["percentage"] ?? null)) {
                    // line 31
                    echo "          <span class=\"webform-progress__percentage\">(";
                    echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed(($context["percentage"] ?? null)), "html", null, true);
                    echo ")</span>
        ";
                }
                // line 33
                echo "      ";
            } else {
                // line 34
                echo "        <span class=\"webform-progress__percentage\">";
                echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed(($context["percentage"] ?? null)), "html", null, true);
                echo "</span>
      ";
            }
            // line 36
            echo "    </div>
  ";
        }
        // line 38
        echo "
</div>
";
    }

    public function getTemplateName()
    {
        return "modules/contrib/webform/templates/webform-progress.html.twig";
    }

    public function isTraitable()
    {
        return false;
    }

    public function getDebugInfo()
    {
        return array (  100 => 38,  96 => 36,  90 => 34,  87 => 33,  81 => 31,  79 => 30,  74 => 29,  72 => 28,  69 => 27,  67 => 26,  62 => 24,  55 => 20,);
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
 * Default theme implementation for webform wizard progress.
 *
 * Available variables:
 * - webform: A webform.
 * - pages: Array of wizard pages.
 * - current_page: Current wizard page.
 * - total_pages: Current wizard page.
 * - summary: Summary of progress.
 * - percentage: Percentage completed.
 * - bar: A progress bar.
 *
 * @see template_preprocess_webform_progress()
 *
 * @ingroup themeable
 */
#}
{{ attach_library('webform/webform.progress') }}

<div class=\"webform-progress\">

  {{ bar }}

  {% if summary or percentage %}
    <div class=\"webform-progress__status\">
      {% if summary %}
        <span class=\"webform-progress__summary\">{{ summary }}</span>
        {% if percentage %}
          <span class=\"webform-progress__percentage\">({{ percentage }})</span>
        {% endif %}
      {% else %}
        <span class=\"webform-progress__percentage\">{{ percentage }}</span>
      {% endif %}
    </div>
  {% endif %}

</div>
", "modules/contrib/webform/templates/webform-progress.html.twig", "/var/www/html/tbs-proto1.openplus.ca/modules/contrib/webform/templates/webform-progress.html.twig");
    }
}
