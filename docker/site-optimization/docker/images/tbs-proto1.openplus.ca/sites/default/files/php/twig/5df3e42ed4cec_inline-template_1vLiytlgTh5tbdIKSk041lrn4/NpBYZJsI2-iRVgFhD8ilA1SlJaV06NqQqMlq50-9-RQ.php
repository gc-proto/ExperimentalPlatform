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

/* {# inline_template_start #}{%if raw_arguments.uid == raw_arguments.null%}{{"Content you created"|t}}{%else%}{{"Content created by user"|t}}{%endif%} */
class __TwigTemplate_d9bfad13f0ec1b482b70b58a1474515489139d6656c4cef1a22ba9e5a4d48ea7 extends \Twig\Template
{
    public function __construct(Environment $env)
    {
        parent::__construct($env);

        $this->parent = false;

        $this->blocks = [
        ];
        $this->sandbox = $this->env->getExtension('\Twig\Extension\SandboxExtension');
        $tags = ["if" => 1];
        $filters = ["t" => 1];
        $functions = [];

        try {
            $this->sandbox->checkSecurity(
                ['if'],
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
        // line 1
        if (($this->getAttribute(($context["raw_arguments"] ?? null), "uid", []) == $this->getAttribute(($context["raw_arguments"] ?? null), "null", []))) {
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->renderVar(t("Content you created"));
        } else {
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->renderVar(t("Content created by user"));
        }
    }

    public function getTemplateName()
    {
        return "{# inline_template_start #}{%if raw_arguments.uid == raw_arguments.null%}{{\"Content you created\"|t}}{%else%}{{\"Content created by user\"|t}}{%endif%}";
    }

    public function isTraitable()
    {
        return false;
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
        return new Source("{# inline_template_start #}{%if raw_arguments.uid == raw_arguments.null%}{{\"Content you created\"|t}}{%else%}{{\"Content created by user\"|t}}{%endif%}", "{# inline_template_start #}{%if raw_arguments.uid == raw_arguments.null%}{{\"Content you created\"|t}}{%else%}{{\"Content created by user\"|t}}{%endif%}", "");
    }
}
