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

/* themes/custom/canada_experiments/templates/menu/menu--main--gcweb.html.twig */
class __TwigTemplate_5be31d0ff00f2199d7f578236ad568178d99175cb609c5f0c21db91e74b4067c extends \Twig\Template
{
    public function __construct(Environment $env)
    {
        parent::__construct($env);

        $this->parent = false;

        $this->blocks = [
        ];
        $this->sandbox = $this->env->getExtension('\Twig\Extension\SandboxExtension');
        $tags = ["import" => 18, "macro" => 26, "if" => 28, "for" => 40, "set" => 42];
        $filters = ["clean_class" => 24];
        $functions = ["link" => 53];

        try {
            $this->sandbox->checkSecurity(
                ['import', 'macro', 'if', 'for', 'set'],
                ['clean_class'],
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
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->renderVar($context["menus"]->getmenu_links(($context["items"] ?? null), ($context["attributes"] ?? null), 0, \Drupal\Component\Utility\Html::getClass($this->sandbox->ensureToStringAllowed(($context["menu_name"] ?? null))), ($context["gcweb_cdn"] ?? null), ($context["gcweb_cdn_url"] ?? null), ($context["language"] ?? null)));
        echo "

";
    }

    // line 26
    public function getmenu_links($__items__ = null, $__attributes__ = null, $__menu_level__ = null, $__menu_name__ = null, $__gcweb_cdn__ = null, $__gcweb_cdn_url__ = null, $__language__ = null, ...$__varargs__)
    {
        $context = $this->env->mergeGlobals([
            "items" => $__items__,
            "attributes" => $__attributes__,
            "menu_level" => $__menu_level__,
            "menu_name" => $__menu_name__,
            "gcweb_cdn" => $__gcweb_cdn__,
            "gcweb_cdn_url" => $__gcweb_cdn_url__,
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
                if ((($context["menu_level"] ?? null) == 0)) {
                    // line 30
                    echo "      ";
                    if ((($context["gcweb_cdn"] ?? null) && ($context["gcweb_cdn_url"] ?? null))) {
                        // line 31
                        echo "        <ul role=\"menu\" aria-orientation=\"vertical\" data-ajax-replace=\"";
                        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed(($context["gcweb_cdn_url"] ?? null)), "html", null, true);
                        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed(($context["language"] ?? null)), "html", null, true);
                        echo ".html\">
      ";
                    } else {
                        // line 33
                        echo "        <ul role=\"menu\" aria-orientation=\"vertical\">
      ";
                    }
                    // line 35
                    echo "    ";
                } elseif ((($context["menu_level"] ?? null) == 1)) {
                    // line 36
                    echo "      <ul";
                    echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute($this->getAttribute(($context["attributes"] ?? null), "addClass", [0 => "sm", 1 => "list-unstyled"], "method"), "removeClass", [0 => "menu", 1 => "list-inline"], "method")), "html", null, true);
                    echo ">
    ";
                } else {
                    // line 38
                    echo "      <ul";
                    echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["attributes"] ?? null), "addClass", [0 => "dropdown-menu", 1 => ($context["menu_name"] ?? null)], "method")), "html", null, true);
                    echo ">
    ";
                }
                // line 40
                echo "    ";
                $context['_parent'] = $context;
                $context['_seq'] = twig_ensure_traversable(($context["items"] ?? null));
                foreach ($context['_seq'] as $context["_key"] => $context["item"]) {
                    // line 41
                    echo "      ";
                    // line 42
                    $context["item_classes"] = [0 => (($this->getAttribute(                    // line 43
$context["item"], "is_expanded", [])) ? ("expanded") : ("")), 1 => ((($this->getAttribute(                    // line 44
$context["item"], "is_expanded", []) && (($context["menu_level"] ?? null) == 0))) ? ("dropdown") : ("")), 2 => (($this->getAttribute(                    // line 45
$context["item"], "in_active_trail", [])) ? ("active") : (""))];
                    // line 48
                    echo "      ";
                    if (((($context["menu_level"] ?? null) == 0) && $this->getAttribute($context["item"], "is_expanded", []))) {
                        // line 49
                        echo "        <li";
                        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute($this->getAttribute($context["item"], "attributes", []), "addClass", [0 => ($context["item_classes"] ?? null)], "method")), "html", null, true);
                        echo " role=\"presentation\">
        <a href=\"";
                        // line 50
                        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute($context["item"], "url", [])), "html", null, true);
                        echo "\" class=\"dropdown-toggle item\" data-target=\"#\" data-toggle=\"dropdown\" role=\"menuitem\">";
                        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute($context["item"], "title", [])), "html", null, true);
                        echo "</a>
      ";
                    } else {
                        // line 52
                        echo "        <li";
                        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute($this->getAttribute($context["item"], "attributes", []), "addClass", [0 => ($context["item_classes"] ?? null)], "method")), "html", null, true);
                        echo " role=\"presentation\">
        ";
                        // line 53
                        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->env->getExtension('Drupal\Core\Template\TwigExtension')->getLink($this->sandbox->ensureToStringAllowed($this->getAttribute($context["item"], "title", [])), $this->sandbox->ensureToStringAllowed($this->getAttribute($context["item"], "url", [])), ["role" => [0 => "menuitem"]]), "html", null, true);
                        echo "
      ";
                    }
                    // line 55
                    echo "      ";
                    if ($this->getAttribute($context["item"], "below", [])) {
                        // line 56
                        echo "        ";
                        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->renderVar($context["menus"]->getmenu_links($this->getAttribute($context["item"], "below", []), $this->getAttribute(($context["attributes"] ?? null), "removeClass", [0 => "nav", 1 => "navbar-nav"], "method"), (($context["menu_level"] ?? null) + 1)));
                        echo "
      ";
                    }
                    // line 58
                    echo "      </li>
    ";
                }
                $_parent = $context['_parent'];
                unset($context['_seq'], $context['_iterated'], $context['_key'], $context['item'], $context['_parent'], $context['loop']);
                $context = array_intersect_key($context, $_parent) + $_parent;
                // line 60
                echo "    </ul>
  ";
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
        return "themes/custom/canada_experiments/templates/menu/menu--main--gcweb.html.twig";
    }

    public function isTraitable()
    {
        return false;
    }

    public function getDebugInfo()
    {
        return array (  176 => 60,  169 => 58,  163 => 56,  160 => 55,  155 => 53,  150 => 52,  143 => 50,  138 => 49,  135 => 48,  133 => 45,  132 => 44,  131 => 43,  130 => 42,  128 => 41,  123 => 40,  117 => 38,  111 => 36,  108 => 35,  104 => 33,  97 => 31,  94 => 30,  91 => 29,  88 => 28,  85 => 27,  67 => 26,  60 => 24,  57 => 19,  55 => 18,);
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
{{ menus.menu_links(items, attributes, 0, menu_name|clean_class, gcweb_cdn, gcweb_cdn_url, language) }}

{% macro menu_links(items, attributes, menu_level, menu_name, gcweb_cdn, gcweb_cdn_url, language) %}
  {% import _self as menus %}
  {% if items %}
    {% if menu_level == 0 %}
      {% if gcweb_cdn and gcweb_cdn_url %}
        <ul role=\"menu\" aria-orientation=\"vertical\" data-ajax-replace=\"{{ gcweb_cdn_url }}{{ language }}.html\">
      {% else %}
        <ul role=\"menu\" aria-orientation=\"vertical\">
      {% endif %}
    {% elseif menu_level == 1 %}
      <ul{{ attributes.addClass('sm', 'list-unstyled').removeClass('menu', 'list-inline') }}>
    {% else %}
      <ul{{ attributes.addClass('dropdown-menu', menu_name) }}>
    {% endif %}
    {% for item in items %}
      {%
        set item_classes = [
          item.is_expanded? 'expanded',
          item.is_expanded and menu_level == 0 ? 'dropdown',
          item.in_active_trail ? 'active',
        ]
      %}
      {% if menu_level == 0 and item.is_expanded %}
        <li{{ item.attributes.addClass(item_classes) }} role=\"presentation\">
        <a href=\"{{ item.url }}\" class=\"dropdown-toggle item\" data-target=\"#\" data-toggle=\"dropdown\" role=\"menuitem\">{{ item.title }}</a>
      {% else %}
        <li{{ item.attributes.addClass(item_classes) }} role=\"presentation\">
        {{ link(item.title, item.url, {'role':['menuitem']}) }}
      {% endif %}
      {% if item.below %}
        {{ menus.menu_links(item.below, attributes.removeClass('nav', 'navbar-nav'), menu_level + 1) }}
      {% endif %}
      </li>
    {% endfor %}
    </ul>
  {% endif %}
{% endmacro %}
", "themes/custom/canada_experiments/templates/menu/menu--main--gcweb.html.twig", "/var/www/html/tbs-proto1.openplus.ca/themes/custom/canada_experiments/templates/menu/menu--main--gcweb.html.twig");
    }
}
