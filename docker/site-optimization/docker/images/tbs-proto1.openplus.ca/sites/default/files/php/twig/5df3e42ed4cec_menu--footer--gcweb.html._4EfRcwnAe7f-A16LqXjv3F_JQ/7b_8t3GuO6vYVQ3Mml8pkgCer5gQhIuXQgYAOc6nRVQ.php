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

/* themes/custom/canada_experiments/templates/menu/menu--footer--gcweb.html.twig */
class __TwigTemplate_d5c22dd903d9f1c181aff92eab87b675cf692a9273b325b896fa393f6b911fea extends \Twig\Template
{
    public function __construct(Environment $env)
    {
        parent::__construct($env);

        $this->parent = false;

        $this->blocks = [
        ];
        $this->sandbox = $this->env->getExtension('\Twig\Extension\SandboxExtension');
        $tags = ["import" => 18, "macro" => 26, "if" => 28, "for" => 35];
        $filters = [];
        $functions = ["link" => 42];

        try {
            $this->sandbox->checkSecurity(
                ['import', 'macro', 'if', 'for'],
                [],
                ['link']
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
        // line 18
        $context["menus"] = $this;
        // line 19
        echo "
";
        // line 24
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->renderVar($context["menus"]->getmenu_links(($context["items"] ?? null), ($context["attributes"] ?? null), 0, ($context["library_path"] ?? null), ($context["gcweb"] ?? null), ($context["language"] ?? null)));
        echo "

";
    }

    // line 26
    public function getmenu_links($__items__ = null, $__attributes__ = null, $__menu_level__ = null, $__library_path__ = null, $__gcweb__ = null, $__language__ = null, ...$__varargs__)
    {
        $context = $this->env->mergeGlobals([
            "items" => $__items__,
            "attributes" => $__attributes__,
            "menu_level" => $__menu_level__,
            "library_path" => $__library_path__,
            "gcweb" => $__gcweb__,
            "language" => $__language__,
            "varargs" => $__varargs__,
        ]);

        $blocks = [];

        ob_start();
        try {
            // line 27
            echo "  ";
            $context["menus"] = $this;
            // line 28
            echo "  ";
            if (($context["items"] ?? null)) {
                // line 29
                echo "    ";
                if ($this->getAttribute(($context["gcweb"] ?? null), "footer", [])) {
                    // line 30
                    echo "     <ul class=\"list-unstyled colcount-sm-2 colcount-md-3\">
    ";
                }
                // line 32
                echo "    ";
                if ((($context["menu_level"] ?? null) != 0)) {
                    // line 33
                    echo "      <ul";
                    echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["attributes"] ?? null), "addClass", [0 => "list-unstyled"], "method")), "html", null, true);
                    echo ">
    ";
                }
                // line 35
                echo "    ";
                $context['_parent'] = $context;
                $context['_seq'] = twig_ensure_traversable(($context["items"] ?? null));
                foreach ($context['_seq'] as $context["_key"] => $context["item"]) {
                    // line 36
                    echo "      ";
                    // line 37
                    echo "      ";
                    if (((($context["menu_level"] ?? null) == 0) && $this->getAttribute($context["item"], "is_expanded", []))) {
                        // line 38
                        echo "        <section";
                        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute($this->getAttribute($context["item"], "attributes", []), "addClass", [0 => "col-sm-3"], "method")), "html", null, true);
                        echo ">
        <h3>";
                        // line 39
                        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute($context["item"], "title", [])), "html", null, true);
                        echo "</h3>
      ";
                    } else {
                        // line 41
                        echo "        <li";
                        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute($context["item"], "attributes", [])), "html", null, true);
                        echo ">
        ";
                        // line 42
                        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->env->getExtension('Drupal\Core\Template\TwigExtension')->getLink($this->sandbox->ensureToStringAllowed($this->getAttribute($context["item"], "title", [])), $this->sandbox->ensureToStringAllowed($this->getAttribute($context["item"], "url", []))), "html", null, true);
                        echo "
      ";
                    }
                    // line 44
                    echo "      ";
                    if ($this->getAttribute($context["item"], "below", [])) {
                        // line 45
                        echo "        ";
                        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->renderVar($context["menus"]->getmenu_links($this->getAttribute($context["item"], "below", []), $this->getAttribute(($context["attributes"] ?? null), "removeClass", [0 => "nav"], "method"), (($context["menu_level"] ?? null) + 1), ($context["library_path"] ?? null), ($context["gcweb"] ?? null), ($context["language"] ?? null)));
                        echo "
      ";
                    }
                    // line 47
                    echo "      ";
                    if (((($context["menu_level"] ?? null) == 0) && $this->getAttribute($context["item"], "is_expanded", []))) {
                        // line 48
                        echo "        </section>
      ";
                    } else {
                        // line 50
                        echo "        </li>
      ";
                    }
                    // line 52
                    echo "    ";
                }
                $_parent = $context['_parent'];
                unset($context['_seq'], $context['_iterated'], $context['_key'], $context['item'], $context['_parent'], $context['loop']);
                $context = array_intersect_key($context, $_parent) + $_parent;
                // line 53
                echo "    ";
                if ((($context["menu_level"] ?? null) != 0)) {
                    // line 54
                    echo "      </ul>
    ";
                }
                // line 56
                echo "    ";
                if ($this->getAttribute(($context["gcweb"] ?? null), "footer", [])) {
                    // line 57
                    echo "    </ul>
    ";
                }
                // line 59
                echo "  ";
            }
        } catch (\Exception $e) {
            ob_end_clean();

            throw $e;
        } catch (\Throwable $e) {
            ob_end_clean();

            throw $e;
        }

        return ('' === $tmp = ob_get_clean()) ? '' : new Markup($tmp, $this->env->getCharset());
    }

    public function getTemplateName()
    {
        return "themes/custom/canada_experiments/templates/menu/menu--footer--gcweb.html.twig";
    }

    public function isTraitable()
    {
        return false;
    }

    public function getDebugInfo()
    {
        return array (  176 => 59,  172 => 57,  169 => 56,  165 => 54,  162 => 53,  156 => 52,  152 => 50,  148 => 48,  145 => 47,  139 => 45,  136 => 44,  131 => 42,  126 => 41,  121 => 39,  116 => 38,  113 => 37,  111 => 36,  106 => 35,  100 => 33,  97 => 32,  93 => 30,  90 => 29,  87 => 28,  84 => 27,  67 => 26,  60 => 24,  57 => 19,  55 => 18,);
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
 * Default theme implementation to display a menu.
 *
 * Available variables:
 * - menu_name: The machine name of the menu.
 * - items: A nested list of menu items. Each menu item contains:
 *   - attributes: HTML attributes for the menu item.
 *   - below: The menu item child items.
 *   - title: The menu link title.
 *   - url: The menu link url, instance of \\Drupal\\Core\\Url
 *   - localized_options: Menu link localized options.
 *
 * @ingroup templates
 */
#}
{% import _self as menus %}

{#
  We call a macro which calls itself to render the full tree.
  @see http://twig.sensiolabs.org/doc/tags/macro.html
#}
{{ menus.menu_links(items, attributes, 0, library_path, gcweb, language) }}

{% macro menu_links(items, attributes, menu_level, library_path, gcweb, language) %}
  {% import _self as menus %}
  {% if items %}
    {% if gcweb.footer %}
     <ul class=\"list-unstyled colcount-sm-2 colcount-md-3\">
    {% endif %}
    {% if menu_level != 0 %}
      <ul{{ attributes.addClass('list-unstyled') }}>
    {% endif %}
    {% for item in items %}
      {# default menu handling #}
      {% if menu_level == 0 and item.is_expanded %}
        <section{{ item.attributes.addClass('col-sm-3') }}>
        <h3>{{ item.title }}</h3>
      {% else %}
        <li{{ item.attributes }}>
        {{ link(item.title, item.url) }}
      {% endif %}
      {% if item.below %}
        {{ menus.menu_links(item.below, attributes.removeClass('nav'), menu_level + 1, library_path, gcweb, language) }}
      {% endif %}
      {% if menu_level == 0 and item.is_expanded %}
        </section>
      {% else %}
        </li>
      {% endif %}
    {% endfor %}
    {% if menu_level != 0 %}
      </ul>
    {% endif %}
    {% if gcweb.footer %}
    </ul>
    {% endif %}
  {% endif %}
{% endmacro %}
", "themes/custom/canada_experiments/templates/menu/menu--footer--gcweb.html.twig", "/var/www/html/tbs-proto1.openplus.ca/themes/custom/canada_experiments/templates/menu/menu--footer--gcweb.html.twig");
    }
}
