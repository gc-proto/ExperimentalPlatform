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

/* themes/contrib/wxt_bootstrap/templates/3.0.0/bs-2col-bricked.html.twig */
class __TwigTemplate_2d4b155960006a392b5e0de1eab838c44a87862da089218187bd115ca5a1ee56 extends \Twig\Template
{
    public function __construct(Environment $env)
    {
        parent::__construct($env);

        $this->parent = false;

        $this->blocks = [
        ];
        $this->sandbox = $this->env->getExtension('\Twig\Extension\SandboxExtension');
        $tags = ["if" => 28];
        $filters = [];
        $functions = [];

        try {
            $this->sandbox->checkSecurity(
                ['if'],
                [],
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
        // line 25
        echo "<";
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed(($context["wrapper"] ?? null)), "html", null, true);
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed(($context["attributes"] ?? null)), "html", null, true);
        echo ">
  ";
        // line 26
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["title_suffix"] ?? null), "contextual_links", [])), "html", null, true);
        echo "

  ";
        // line 28
        if ($this->getAttribute(($context["top"] ?? null), "content", [])) {
            // line 29
            echo "  <";
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["top"] ?? null), "wrapper", [])), "html", null, true);
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["top"] ?? null), "attributes", [])), "html", null, true);
            echo ">
    ";
            // line 30
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["top"] ?? null), "content", [])), "html", null, true);
            echo "
  </";
            // line 31
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["top"] ?? null), "wrapper", [])), "html", null, true);
            echo ">
  ";
        }
        // line 33
        echo "
  ";
        // line 34
        if ($this->getAttribute(($context["top_left"] ?? null), "content", [])) {
            // line 35
            echo "  <";
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["top_left"] ?? null), "wrapper", [])), "html", null, true);
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["top_left"] ?? null), "attributes", [])), "html", null, true);
            echo ">
    ";
            // line 36
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["top_left"] ?? null), "content", [])), "html", null, true);
            echo "
  </";
            // line 37
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["top_left"] ?? null), "wrapper", [])), "html", null, true);
            echo ">
  ";
        }
        // line 39
        echo "
  ";
        // line 40
        if ($this->getAttribute(($context["top_right"] ?? null), "content", [])) {
            // line 41
            echo "  <";
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["top_right"] ?? null), "wrapper", [])), "html", null, true);
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["top_right"] ?? null), "attributes", [])), "html", null, true);
            echo ">
    ";
            // line 42
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["top_right"] ?? null), "content", [])), "html", null, true);
            echo "
  </";
            // line 43
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["top_right"] ?? null), "wrapper", [])), "html", null, true);
            echo ">
  ";
        }
        // line 45
        echo "
  ";
        // line 46
        if ($this->getAttribute(($context["middle"] ?? null), "content", [])) {
            // line 47
            echo "  <";
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["middle"] ?? null), "wrapper", [])), "html", null, true);
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["middle"] ?? null), "attributes", [])), "html", null, true);
            echo ">
    ";
            // line 48
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["middle"] ?? null), "content", [])), "html", null, true);
            echo "
  </";
            // line 49
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["middle"] ?? null), "wrapper", [])), "html", null, true);
            echo ">
  ";
        }
        // line 51
        echo "
";
        // line 52
        if (($this->getAttribute(($context["bottom_left"] ?? null), "content", []) || $this->getAttribute(($context["bottom_right"] ?? null), "content", []))) {
            // line 53
            echo "  <div class=\"wxt-eqht col-md-12\">
    <div class=\"row\">
      ";
            // line 55
            if ($this->getAttribute(($context["bottom_left"] ?? null), "content", [])) {
                // line 56
                echo "      <";
                echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["bottom_left"] ?? null), "wrapper", [])), "html", null, true);
                echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["bottom_left"] ?? null), "attributes", [])), "html", null, true);
                echo ">
        ";
                // line 57
                echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["bottom_left"] ?? null), "content", [])), "html", null, true);
                echo "
      </";
                // line 58
                echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["bottom_left"] ?? null), "wrapper", [])), "html", null, true);
                echo ">
      ";
            }
            // line 60
            echo "
      ";
            // line 61
            if ($this->getAttribute(($context["bottom_right"] ?? null), "content", [])) {
                // line 62
                echo "      <";
                echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["bottom_right"] ?? null), "wrapper", [])), "html", null, true);
                echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["bottom_right"] ?? null), "attributes", [])), "html", null, true);
                echo ">
        ";
                // line 63
                echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["bottom_right"] ?? null), "content", [])), "html", null, true);
                echo "
      </";
                // line 64
                echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["bottom_right"] ?? null), "wrapper", [])), "html", null, true);
                echo ">
      ";
            }
            // line 66
            echo "    </div>
  </div>
";
        }
        // line 69
        echo "
  ";
        // line 70
        if ($this->getAttribute(($context["bottom"] ?? null), "content", [])) {
            // line 71
            echo "  <";
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["bottom"] ?? null), "wrapper", [])), "html", null, true);
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["bottom"] ?? null), "attributes", [])), "html", null, true);
            echo ">
    ";
            // line 72
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["bottom"] ?? null), "content", [])), "html", null, true);
            echo "
  </";
            // line 73
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["bottom"] ?? null), "wrapper", [])), "html", null, true);
            echo ">
  ";
        }
        // line 75
        echo "
</";
        // line 76
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed(($context["wrapper"] ?? null)), "html", null, true);
        echo ">
";
    }

    public function getTemplateName()
    {
        return "themes/contrib/wxt_bootstrap/templates/3.0.0/bs-2col-bricked.html.twig";
    }

    public function isTraitable()
    {
        return false;
    }

    public function getDebugInfo()
    {
        return array (  217 => 76,  214 => 75,  209 => 73,  205 => 72,  199 => 71,  197 => 70,  194 => 69,  189 => 66,  184 => 64,  180 => 63,  174 => 62,  172 => 61,  169 => 60,  164 => 58,  160 => 57,  154 => 56,  152 => 55,  148 => 53,  146 => 52,  143 => 51,  138 => 49,  134 => 48,  128 => 47,  126 => 46,  123 => 45,  118 => 43,  114 => 42,  108 => 41,  106 => 40,  103 => 39,  98 => 37,  94 => 36,  88 => 35,  86 => 34,  83 => 33,  78 => 31,  74 => 30,  68 => 29,  66 => 28,  61 => 26,  55 => 25,);
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
 * Bootstrap Layouts: \"2 Columns (stacked)\" template.
 *
 * Available layout variables:
 * - wrapper: Wrapper element for the layout container.
 * - attributes: Wrapper attributes for the layout container.
 *
 * Available region variables:
 * - top
 * - top_left
 * - top_right
 * - middle
 * - bottom_left
 * - bottom_right
 * - bottom
 *
 * Each region variable contains the following properties:
 * - wrapper: The HTML element to use to wrap this region.
 * - attributes: The HTML attributes to use on the wrapper for this region.
 * - content: The content to go inside the wrapper for this region.
 */
#}
<{{ wrapper }}{{ attributes }}>
  {{ title_suffix.contextual_links }}

  {% if top.content %}
  <{{ top.wrapper }}{{ top.attributes }}>
    {{ top.content }}
  </{{ top.wrapper }}>
  {% endif %}

  {% if top_left.content %}
  <{{ top_left.wrapper }}{{ top_left.attributes }}>
    {{ top_left.content }}
  </{{ top_left.wrapper }}>
  {% endif %}

  {% if top_right.content %}
  <{{ top_right.wrapper }}{{ top_right.attributes }}>
    {{ top_right.content }}
  </{{ top_right.wrapper }}>
  {% endif %}

  {% if middle.content %}
  <{{ middle.wrapper }}{{ middle.attributes }}>
    {{ middle.content }}
  </{{ middle.wrapper }}>
  {% endif %}

{% if (bottom_left.content) or (bottom_right.content) %}
  <div class=\"wxt-eqht col-md-12\">
    <div class=\"row\">
      {% if bottom_left.content %}
      <{{ bottom_left.wrapper }}{{ bottom_left.attributes }}>
        {{ bottom_left.content }}
      </{{ bottom_left.wrapper }}>
      {% endif %}

      {% if bottom_right.content %}
      <{{ bottom_right.wrapper }}{{ bottom_right.attributes }}>
        {{ bottom_right.content }}
      </{{ bottom_right.wrapper }}>
      {% endif %}
    </div>
  </div>
{% endif %}

  {% if bottom.content %}
  <{{ bottom.wrapper }}{{ bottom.attributes }}>
    {{ bottom.content }}
  </{{ bottom.wrapper }}>
  {% endif %}

</{{ wrapper }}>
", "themes/contrib/wxt_bootstrap/templates/3.0.0/bs-2col-bricked.html.twig", "/var/www/html/tbs-proto1.openplus.ca/themes/contrib/wxt_bootstrap/templates/3.0.0/bs-2col-bricked.html.twig");
    }
}
