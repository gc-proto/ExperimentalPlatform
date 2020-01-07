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

/* {# inline_template_start #}<a href="#" class="cke-icon-only" role="button" title="Language" aria-label="Language"><span class="cke_button_icon cke_button__language_icon">Language</span></a> */
class __TwigTemplate_4b0cb8b39c4bad6cc5c039f22a93398ccc107cdb3b9605fc4fa45d83112f97a8 extends \Twig\Template
{
    public function __construct(Environment $env)
    {
        parent::__construct($env);

        $this->parent = false;

        $this->blocks = [
        ];
        $this->sandbox = $this->env->getExtension('\Twig\Extension\SandboxExtension');
        $tags = [];
        $filters = [];
        $functions = [];

        try {
            $this->sandbox->checkSecurity(
                [],
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
        echo "<a href=\"#\" class=\"cke-icon-only\" role=\"button\" title=\"Language\" aria-label=\"Language\"><span class=\"cke_button_icon cke_button__language_icon\">Language</span></a>";
    }

    public function getTemplateName()
    {
        return "{# inline_template_start #}<a href=\"#\" class=\"cke-icon-only\" role=\"button\" title=\"Language\" aria-label=\"Language\"><span class=\"cke_button_icon cke_button__language_icon\">Language</span></a>";
    }

    public function getDebugInfo()
    {
        return array (  55 => 1,);
    }

    /** @deprecated since 1.27 (to be removed in 2.0). Use getSourceContext() instead */
    public function getSource()
    {
        @trigger_error('The '.__METHOD__.' method is deprecated since version 1.27 and will be removed in 2.0. Use getSourceContext() instead.', E_USER_DEPRECATED);

        return $this->getSourceContext()->getCode();
    }

    public function getSourceContext()
    {
        return new Source("{# inline_template_start #}<a href=\"#\" class=\"cke-icon-only\" role=\"button\" title=\"Language\" aria-label=\"Language\"><span class=\"cke_button_icon cke_button__language_icon\">Language</span></a>", "{# inline_template_start #}<a href=\"#\" class=\"cke-icon-only\" role=\"button\" title=\"Language\" aria-label=\"Language\"><span class=\"cke_button_icon cke_button__language_icon\">Language</span></a>", "");
    }
}
