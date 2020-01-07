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

/* {# inline_template_start #}{%if raw_arguments.uid == raw_arguments.null%}{{"Your activity"|t}}{%else%}{{"User's activity"|t}}{%endif%} */
class __TwigTemplate_198d7cf610359af714151081e94de9e759eb84e8a12596c236f0855e78675eb8 extends \Twig\Template
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
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->renderVar(t("Your activity"));
        } else {
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->renderVar(t("User's activity"));
        }
    }

    public function getTemplateName()
    {
        return "{# inline_template_start #}{%if raw_arguments.uid == raw_arguments.null%}{{\"Your activity\"|t}}{%else%}{{\"User's activity\"|t}}{%endif%}";
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
        return new Source("{# inline_template_start #}{%if raw_arguments.uid == raw_arguments.null%}{{\"Your activity\"|t}}{%else%}{{\"User's activity\"|t}}{%endif%}", "{# inline_template_start #}{%if raw_arguments.uid == raw_arguments.null%}{{\"Your activity\"|t}}{%else%}{{\"User's activity\"|t}}{%endif%}", "");
    }
}
