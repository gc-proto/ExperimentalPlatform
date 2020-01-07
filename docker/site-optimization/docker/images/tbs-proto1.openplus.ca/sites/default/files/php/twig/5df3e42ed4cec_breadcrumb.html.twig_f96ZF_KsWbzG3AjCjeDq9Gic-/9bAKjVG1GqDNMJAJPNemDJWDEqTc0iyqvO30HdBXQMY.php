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

/* themes/custom/canada_experiments/templates/system/breadcrumb.html.twig */
class __TwigTemplate_51d8991169c5a640ca32f83537cb47c118cb737c05a92f26fda2fd948b307cbe extends \Twig\Template
{
    public function __construct(Environment $env)
    {
        parent::__construct($env);

        $this->parent = false;

        $this->blocks = [
        ];
        $this->sandbox = $this->env->getExtension('\Twig\Extension\SandboxExtension');
        $tags = ["set" => 12, "if" => 13, "for" => 18];
        $filters = ["t" => 15];
        $functions = [];

        try {
            $this->sandbox->checkSecurity(
                ['set', 'if', 'for'],
                ['t'],
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
        // line 12
        $context["container"] = (($this->getAttribute($this->getAttribute(($context["theme"] ?? null), "settings", []), "fluid_container", [])) ? ("container-fluid") : ("container"));
        // line 13
        if (($context["breadcrumb"] ?? null)) {
            // line 14
            echo "  <nav id=\"wb-bc\" property=\"breadcrumb\">
    <h2>";
            // line 15
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->renderVar(t("You are here"));
            echo "</h2>
    <div class=\"";
            // line 16
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed(($context["container"] ?? null)), "html", null, true);
            echo "\">
      <ol class=\"breadcrumb\">
        ";
            // line 18
            $context['_parent'] = $context;
            $context['_seq'] = twig_ensure_traversable(($context["breadcrumb"] ?? null));
            foreach ($context['_seq'] as $context["_key"] => $context["item"]) {
                // line 19
                echo "          <li ";
                echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute($context["item"], "attributes", [])), "html", null, true);
                echo ">
            ";
                // line 20
                if ($this->getAttribute($context["item"], "url", [])) {
                    // line 21
                    echo "              <a href=\"";
                    echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute($context["item"], "url", [])), "html", null, true);
                    echo "\">";
                    echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute($context["item"], "text", [])), "html", null, true);
                    echo "</a>
            ";
                } else {
                    // line 23
                    echo "              ";
                    echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute($context["item"], "text", [])), "html", null, true);
                    echo "
            ";
                }
                // line 25
                echo "          </li>
        ";
            }
            $_parent = $context['_parent'];
            unset($context['_seq'], $context['_iterated'], $context['_key'], $context['item'], $context['_parent'], $context['loop']);
            $context = array_intersect_key($context, $_parent) + $_parent;
            // line 27
            echo "      </ol>
  </div>
</nav>
";
        }
    }

    public function getTemplateName()
    {
        return "themes/custom/canada_experiments/templates/system/breadcrumb.html.twig";
    }

    public function isTraitable()
    {
        return false;
    }

    public function getDebugInfo()
    {
        return array (  103 => 27,  96 => 25,  90 => 23,  82 => 21,  80 => 20,  75 => 19,  71 => 18,  66 => 16,  62 => 15,  59 => 14,  57 => 13,  55 => 12,);
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
 * Default theme implementation for a breadcrumb trail.
 *
 * Available variables:
 * - breadcrumb: Breadcrumb trail items.
 *
 * @ingroup templates
 */
#}
{% set container = theme.settings.fluid_container ? 'container-fluid' : 'container' %}
{% if breadcrumb %}
  <nav id=\"wb-bc\" property=\"breadcrumb\">
    <h2>{{ 'You are here'|t }}</h2>
    <div class=\"{{ container }}\">
      <ol class=\"breadcrumb\">
        {% for item in breadcrumb %}
          <li {{ item.attributes }}>
            {% if item.url %}
              <a href=\"{{ item.url }}\">{{ item.text }}</a>
            {% else %}
              {{ item.text }}
            {% endif %}
          </li>
        {% endfor %}
      </ol>
  </div>
</nav>
{% endif %}
", "themes/custom/canada_experiments/templates/system/breadcrumb.html.twig", "/var/www/html/tbs-proto1.openplus.ca/themes/custom/canada_experiments/templates/system/breadcrumb.html.twig");
    }
}
