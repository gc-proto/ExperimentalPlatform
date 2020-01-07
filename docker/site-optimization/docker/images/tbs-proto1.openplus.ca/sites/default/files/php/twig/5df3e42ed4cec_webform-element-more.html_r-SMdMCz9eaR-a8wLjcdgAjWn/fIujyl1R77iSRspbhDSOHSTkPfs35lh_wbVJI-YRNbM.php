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

/* modules/contrib/webform/templates/webform-element-more.html.twig */
class __TwigTemplate_7c18839f92bf09ca4bbb7a35f90fb8dee3820f6519da40deba0b7ee3ffd11b45 extends \Twig\Template
{
    public function __construct(Environment $env)
    {
        parent::__construct($env);

        $this->parent = false;

        $this->blocks = [
        ];
        $this->sandbox = $this->env->getExtension('\Twig\Extension\SandboxExtension');
        $tags = ["set" => 20];
        $filters = [];
        $functions = ["attach_library" => 18];

        try {
            $this->sandbox->checkSecurity(
                ['set'],
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
        // line 18
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->env->getExtension('Drupal\Core\Template\TwigExtension')->attachLibrary("webform/webform.element.more"), "html", null, true);
        echo "
";
        // line 20
        $context["classes"] = [0 => "js-webform-element-more", 1 => "webform-element-more"];
        // line 25
        echo "<div";
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["attributes"] ?? null), "addClass", [0 => ($context["classes"] ?? null)], "method")), "html", null, true);
        echo ">
  <div class=\"webform-element-more--link\"><a role=\"button\" href=\"#more\">";
        // line 26
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed(($context["more_title"] ?? null)), "html", null, true);
        echo "</a></div>
  <div id=\"";
        // line 27
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["attributes"] ?? null), "id", [])), "html", null, true);
        echo "--content\" class=\"webform-element-more--content\">";
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed(($context["more"] ?? null)), "html", null, true);
        echo "</div>
</div>
";
    }

    public function getTemplateName()
    {
        return "modules/contrib/webform/templates/webform-element-more.html.twig";
    }

    public function isTraitable()
    {
        return false;
    }

    public function getDebugInfo()
    {
        return array (  70 => 27,  66 => 26,  61 => 25,  59 => 20,  55 => 18,);
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
 * Theme implementation for webform element more.
 *
 * Available variables
 * - title: More label.
 * - content: More content.
 *
 * Based on WAI-ARIA Authoring Practices 1.1: Disclosure (Show/Hide)
 *
 * @see https://www.w3.org/TR/wai-aria-practices-1.1/#disclosure
 * @see https://www.w3.org/TR/wai-aria-practices-1.1/examples/disclosure/disclosure-faq.html
 * @see template_preprocess_webform_element_more()
 * @ingroup themeable
 */
#}
{{ attach_library('webform/webform.element.more') }}
{%
set classes = [
  'js-webform-element-more',
  'webform-element-more',
]
%}
<div{{ attributes.addClass(classes) }}>
  <div class=\"webform-element-more--link\"><a role=\"button\" href=\"#more\">{{ more_title }}</a></div>
  <div id=\"{{ attributes.id }}--content\" class=\"webform-element-more--content\">{{ more }}</div>
</div>
", "modules/contrib/webform/templates/webform-element-more.html.twig", "/var/www/html/tbs-proto1.openplus.ca/modules/contrib/webform/templates/webform-element-more.html.twig");
    }
}
