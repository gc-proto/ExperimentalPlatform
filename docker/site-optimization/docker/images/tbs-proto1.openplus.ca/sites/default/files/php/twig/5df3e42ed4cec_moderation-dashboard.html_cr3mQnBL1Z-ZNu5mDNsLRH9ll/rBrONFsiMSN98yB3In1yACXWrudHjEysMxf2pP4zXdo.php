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

/* modules/contrib/moderation_dashboard/templates/moderation-dashboard.html.twig */
class __TwigTemplate_4726e2f81a66281fa1b5d3fc958ff06c620fe3d35286d426f77fa5db5b17cec8 extends \Twig\Template
{
    public function __construct(Environment $env)
    {
        parent::__construct($env);

        $this->parent = false;

        $this->blocks = [
        ];
        $this->sandbox = $this->env->getExtension('\Twig\Extension\SandboxExtension');
        $tags = ["if" => 2];
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
        // line 1
        echo "<div class=\"moderation-dashboard\">
  ";
        // line 2
        if ($this->getAttribute(($context["content"] ?? null), "left", [])) {
            // line 3
            echo "    <div class=\"moderation-dashboard-region\">
      ";
            // line 4
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["content"] ?? null), "left", [])), "html", null, true);
            echo "
    </div>
  ";
        }
        // line 7
        echo "  ";
        if ($this->getAttribute(($context["content"] ?? null), "middle", [])) {
            // line 8
            echo "    <div class=\"moderation-dashboard-region\">
      ";
            // line 9
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["content"] ?? null), "middle", [])), "html", null, true);
            echo "
    </div>
  ";
        }
        // line 12
        echo "  ";
        if ($this->getAttribute(($context["content"] ?? null), "right", [])) {
            // line 13
            echo "    <div class=\"moderation-dashboard-region\">
      ";
            // line 14
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["content"] ?? null), "right", [])), "html", null, true);
            echo "
    </div>
  ";
        }
        // line 17
        echo "</div>
";
    }

    public function getTemplateName()
    {
        return "modules/contrib/moderation_dashboard/templates/moderation-dashboard.html.twig";
    }

    public function isTraitable()
    {
        return false;
    }

    public function getDebugInfo()
    {
        return array (  93 => 17,  87 => 14,  84 => 13,  81 => 12,  75 => 9,  72 => 8,  69 => 7,  63 => 4,  60 => 3,  58 => 2,  55 => 1,);
    }

    /** @deprecated since 1.27 (to be removed in 2.0). Use getSourceContext() instead */
    public function getSource()
    {
        @trigger_error('The '.__METHOD__.' method is deprecated since version 1.27 and will be removed in 2.0. Use getSourceContext() instead.', E_USER_DEPRECATED);

        return $this->getSourceContext()->getCode();
    }

    public function getSourceContext()
    {
        return new Source("<div class=\"moderation-dashboard\">
  {% if content.left %}
    <div class=\"moderation-dashboard-region\">
      {{ content.left }}
    </div>
  {% endif %}
  {% if content.middle %}
    <div class=\"moderation-dashboard-region\">
      {{ content.middle }}
    </div>
  {% endif %}
  {% if content.right %}
    <div class=\"moderation-dashboard-region\">
      {{ content.right }}
    </div>
  {% endif %}
</div>
", "modules/contrib/moderation_dashboard/templates/moderation-dashboard.html.twig", "/var/www/html/tbs-proto1.openplus.ca/modules/contrib/moderation_dashboard/templates/moderation-dashboard.html.twig");
    }
}
